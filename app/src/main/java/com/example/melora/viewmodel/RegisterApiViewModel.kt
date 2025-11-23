package com.example.melora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.remote.dto.RegisterUserDto
import com.example.melora.data.remote.dto.RolDto
import com.example.melora.data.repository.RegisterApiRepository
import com.example.melora.domain.validation.validateEmail
import com.example.melora.domain.validation.validateNickname
import com.example.melora.domain.validation.validatePassword
import com.example.melora.domain.validation.validateConfirmPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Base64

data class RegisterApiUiState(
    val nickname: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nicknameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPassError: String? = null,
    val profilePhotoBase64: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMessage: String? = null
)

class RegisterApiViewModel(
    private val repository: RegisterApiRepository = RegisterApiRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterApiUiState())
    val uiState: StateFlow<RegisterApiUiState> = _uiState

    fun onNicknameChange(value: String) {
        _uiState.update {
            it.copy(
                nickname = value,
                nicknameError = validateNickname(value)
            )
        }
        recomputeCanSubmit()
    }

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(
                email = value,
                emailError = validateEmail(value)
            )
        }
        recomputeCanSubmit()
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                passwordError = validatePassword(value),
                confirmPassError = validateConfirmPassword(value, it.confirmPassword)
            )
        }
        recomputeCanSubmit()
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                confirmPassword = value,
                confirmPassError = validateConfirmPassword(it.password, value)
            )
        }
        recomputeCanSubmit()
    }

    fun setProfilePhoto(byteArray: ByteArray) {
        val encoded = Base64.getEncoder().encodeToString(byteArray)
        _uiState.update { it.copy(profilePhotoBase64 = encoded) }
        recomputeCanSubmit()
    }

    private fun recomputeCanSubmit() {
        val s = _uiState.value

        val can =
            s.nicknameError == null &&
                    s.emailError == null &&
                    s.passwordError == null &&
                    s.confirmPassError == null &&
                    s.nickname.isNotBlank() &&
                    s.email.isNotBlank() &&
                    s.password.isNotBlank() &&
                    s.confirmPassword.isNotBlank()

        _uiState.update { it.copy(canSubmit = can) }
    }

    fun submitRegister() {
        val s = _uiState.value
        if (!s.canSubmit || s.isSubmitting) return

        val dto = RegisterUserDto(
            email = s.email,
            password = s.password,
            rol = RolDto(2),
            nickname = s.nickname,
            profilePhotoBase64 = s.profilePhotoBase64
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val result = repository.register(dto)

            if (result.isSuccess) {
                _uiState.update { it.copy(isSubmitting = false, success = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                    )
                }
            }
        }
    }

    fun clearState() {
        _uiState.value = RegisterApiUiState()
    }
}
