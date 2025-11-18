package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.remote.dto.LoginUserDto
import com.example.melora.data.repository.LoginApiRepository
import com.example.melora.data.storage.UserPreferences
import com.example.melora.domain.validation.validateEmail
import com.example.melora.domain.validation.validatePassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiStateApi(
    val email: String = "",
    val emailError: String? = null,
    val pass: String = "",
    val passError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,

    val errorMessage: String? = null
)

class LoginApiViewModel(
    private val repository: LoginApiRepository = LoginApiRepository(),
    private val prefs: UserPreferences
) : ViewModel() {

    private val _login = MutableStateFlow(LoginUiStateApi())
    val login: StateFlow<LoginUiStateApi> = _login


    fun onEmailChange(value: String) {
        _login.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeCanSubmit()
    }

    fun onPasswordChange(value: String) {
        _login.update { it.copy(pass = value, passError = validatePassword(value)) }
        recomputeCanSubmit()
    }

    private fun recomputeCanSubmit() {
        val s = _login.value

        val can = s.emailError == null &&
                s.passError == null &&
                s.email.isNotBlank() &&
                s.pass.isNotBlank()

        _login.update { it.copy(canSubmit = can) }
    }


    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {

            _login.update {
                it.copy(
                    isSubmitting = true,
                    success = false,
                    errorMessage = null
                )
            }

            val dto = LoginUserDto(
                email = s.email.trim(),
                password = s.pass
            )

            val result = repository.loginUser(dto)

            if (result.isSuccess) {

                val response = result.getOrNull()!!

                val data = response.data

                if (data == null) {
                    _login.update {
                        it.copy(
                            isSubmitting = false,
                            success = false,
                            errorMessage = "Respuesta inválida del servidor"
                        )
                    }
                    return@launch
                }

                prefs.saveUserData(
                    idUser = data.idUser,
                    roleId = data.rolId,
                    nickname = data.nickname,
                    email = data.email,
                    profilePhotoBase64 = data.profilePhotoBase64
                )

                _login.update {
                    it.copy(
                        isSubmitting = false,
                        success = true,
                        errorMessage = null
                    )
                }

            } else {

                _login.update {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMessage = result.exceptionOrNull()?.message
                            ?: "Error al autenticar"
                    )
                }
            }
        }
    }

    fun clearLoginStatus() {
        _login.update { it.copy(success = false, errorMessage = null) }
    }
}
