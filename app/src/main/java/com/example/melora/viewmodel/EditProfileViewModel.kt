package com.example.melora.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.remote.LoginApi
import com.example.melora.data.remote.RegisterApi
import com.example.melora.data.remote.dto.LoginUserDto
import com.example.melora.data.remote.dto.RegisterUserDto
import com.example.melora.data.storage.UserPreferences
import com.example.melora.domain.validation.validateEmail
import com.example.melora.domain.validation.validateNickname
import com.example.melora.domain.validation.validatePassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val nickname: String = "",
    val nicknameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val currentPassword: String = "",
    val currentPasswordError: String? = null,
    val password: String = "",
    val confirmPassword: String = "",
    val passwordError: String? = null,
    val passwordConfirmError: String? = null,
    val profilePictureUrl: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMessage: String? = null
)

class EditProfileViewModel(
    private val registerApi: RegisterApi,
    private val loginApi: LoginApi,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileUiState())
    val state = _state

    private var initialNickname: String? = null
    private var initialEmail: String? = null
    private var initialPhoto: String? = null
    private var initialRoleId: Long? = null

    private var backendPasswordValidated = false

    init {
        viewModelScope.launch {
            val nickname = prefs.nickname.first() ?: ""
            val email = prefs.email.first() ?: ""
            val photo = prefs.profilePicture.first()
            val role = prefs.userRoleId.first() ?: 1L

            initialNickname = nickname
            initialEmail = email
            initialPhoto = photo
            initialRoleId = role

            _state.value = EditProfileUiState(
                nickname = nickname,
                email = email,
                profilePictureUrl = photo
            )
        }
    }


    fun onNicknameChange(value: String) {
        _state.update { it.copy(nickname = value, nicknameError = validateNickname(value)) }
        recomputeCanSubmit()
    }

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeCanSubmit()
    }

    fun onPasswordChange(value: String) {
        val error = if (value.isNotBlank()) validatePassword(value) else null
        _state.update { it.copy(password = value, passwordError = error) }
        recomputeCanSubmit()
    }

    fun onConfirmPasswordChange(value: String) {
        val pass = _state.value.password
        val error = if (value != pass) "Passwords don't match" else null

        _state.update { it.copy(confirmPassword = value, passwordConfirmError = error) }
        recomputeCanSubmit()
    }

    fun onProfilePictureChange(uri: String?) {
        _state.update { it.copy(profilePictureUrl = uri) }
        recomputeCanSubmit()
    }

    fun onCurrentPasswordChange(value: String) {
        _state.update { it.copy(currentPassword = value, currentPasswordError = null) }
        backendPasswordValidated = false

        if (value.isNotBlank()) validateCurrentPassword()
        recomputeCanSubmit()
    }

    private fun validateCurrentPassword() {
        viewModelScope.launch {
            val email = prefs.email.first() ?: return@launch

            val response = loginApi.login(LoginUserDto(email, _state.value.currentPassword))

            backendPasswordValidated = response.isSuccessful

            _state.update {
                it.copy(
                    currentPasswordError = if (!backendPasswordValidated) "Incorrect current password" else null
                )
            }

            recomputeCanSubmit()
        }
    }


    private fun recomputeCanSubmit() {
        val s = _state.value


        val wantsPasswordChange = s.password.isNotBlank() || s.confirmPassword.isNotBlank()


        val basicChanges =
            s.nickname != initialNickname ||
                    s.email != initialEmail ||
                    s.profilePictureUrl != initialPhoto

        // Validaciones básicas
        val noErrors = listOf(
            s.nicknameError,
            s.emailError,
            if (wantsPasswordChange) s.passwordError else null,
            if (wantsPasswordChange) s.passwordConfirmError else null,
            if (wantsPasswordChange) s.currentPasswordError else null
        ).all { it == null }

        // Lógica de habilitación del botón
        val canSubmit = when {
            // Cambios SIN cambiar contraseña
            basicChanges && !wantsPasswordChange -> true

            // Cambios CON cambio de contraseña
            wantsPasswordChange -> {
                basicChanges || s.password.isNotBlank() &&
                        s.currentPassword.isNotBlank() &&
                        backendPasswordValidated &&
                        noErrors
            }

            else -> false
        }

        _state.update { it.copy(canSubmit = canSubmit) }
    }


    fun submitChanges() {
        val s = _state.value
        if (!s.canSubmit) return

        viewModelScope.launch {
            try {
                _state.update { it.copy(isSubmitting = true, errorMessage = null) }

                val idUser = prefs.userId.first() ?: return@launch

                val wantsPasswordChange = s.password.isNotBlank()

                val dto = RegisterUserDto(
                    email = s.email,
                    nickname = s.nickname,
                    rolId = initialRoleId ?: 1L,
                    profilePhotoBase64 = s.profilePictureUrl,

                    password = if (wantsPasswordChange) {
                        s.password
                    } else {
                        s.currentPassword
                    }
                )

                val response = registerApi.updateUser(idUser, dto)

                if (!response.isSuccessful) {
                    _state.update { it.copy(isSubmitting = false, errorMessage = "Could not update profile") }
                    return@launch
                }

                prefs.setNickname(s.nickname)
                prefs.setEmail(s.email)
                prefs.setProfilePicture(s.profilePictureUrl)

                initialNickname = s.nickname
                initialEmail = s.email
                initialPhoto = s.profilePictureUrl

                _state.update {
                    it.copy(
                        isSubmitting = false,
                        success = true,
                        password = "",
                        confirmPassword = "",
                        currentPassword = "",
                        canSubmit = false
                    )
                }

            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false, errorMessage = e.message) }
            }
        }
    }



    fun resetFormToOriginalUser() {
        backendPasswordValidated = false

        _state.update {
            it.copy(
                nickname = initialNickname ?: "",
                email = initialEmail ?: "",
                profilePictureUrl = initialPhoto,
                password = "",
                confirmPassword = "",
                currentPassword = "",
                nicknameError = null,
                emailError = null,
                passwordError = null,
                passwordConfirmError = null,
                currentPasswordError = null,
                isSubmitting = false,
                success = false,
                errorMessage = null,
                canSubmit = false
            )
        }
    }


    fun clearResult() {
        _state.update {
            it.copy(
                success = false,
                errorMessage = null
            )
        }
    }


    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            prefs.clear()
            backendPasswordValidated = false

            _state.value = EditProfileUiState() // Reiniciar todo el formulario
            onLogout()
        }
    }


}

