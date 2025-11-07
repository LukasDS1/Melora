package com.example.melora.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.local.users.UserEntity
import com.example.melora.data.repository.UserRepository
import com.example.melora.data.storage.UserPreferences
import com.example.melora.domain.validation.validateEmail
import com.example.melora.domain.validation.validateNickname
import com.example.melora.domain.validation.validatePassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
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
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
): ViewModel() {

    private val _state = MutableStateFlow(EditProfileUiState())
    val state: StateFlow<EditProfileUiState> = _state

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var currentUserId: Long? = null
    private var originalUser: UserEntity? = null
    private var initialProfilePicture: String? = null

    init {
        viewModelScope.launch {
            userPreferences.userId.collect { id ->
                if (id != null) {
                    loadUserById(id)
                } else {
                    _state.value = EditProfileUiState()
                    originalUser = null
                    currentUserId = null
                }
            }
        }
    }



    fun resetFormToOriginalUser() {
        originalUser?.let { user ->
            _state.value = _state.value.copy(
                nickname = user.nickname,
                email = user.email,
                password = "",
                nicknameError = null,
                emailError = null,
                passwordError = null,
                isSubmitting = false,
                canSubmit = false,
                success = false,
                errorMessage = null,
                profilePictureUrl = user.profilePicture
            )
        }
    }

    private suspend fun loadUserById(id: Long) {
        currentUserId = id
        val user = userRepository.getUserById(id)
        if (user != null) {
            originalUser = user
            initialProfilePicture = user.profilePicture
            _state.value = EditProfileUiState(
                nickname = user.nickname,
                email = user.email,
                profilePictureUrl = user.profilePicture
            )
        }
    }

    fun onCurrentPasswordChange(value: String) {
        _state.update { it.copy(currentPassword = value, currentPasswordError = null) }
        recomputeCanSubmit()
    }

    fun onProfilePictureChange(uriString: String?) {
        _state.update { it.copy(profilePictureUrl = uriString) }
        originalUser = originalUser?.copy(profilePicture = uriString)
        recomputeCanSubmit()

        viewModelScope.launch {
            currentUserId?.let { id ->
                userRepository.updateProfilePicture(id, uriString)
            }
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
        _state.update { it.copy(password = value, passwordError = if (value.isNotBlank()) validatePassword(value) else null) }
        recomputeCanSubmit()
    }

    fun onConfirmPasswordChange(value: String) {
        val password = _state.value.password

        val error = when {
            value.isBlank() && password.isNotBlank() -> "Confirm"
            value != password -> "The password are not the same"
            else -> null
        }
        _state.update {
            it.copy(
                confirmPassword = value,
                passwordConfirmError = error
            )
        }
        recomputeCanSubmit()
    }

    private fun recomputeCanSubmit() {
        val s = _state.value
        val passwordChanging = s.password.isNotBlank()

        val noErrors = listOf(
            s.nicknameError,
            s.emailError,
            s.passwordError,
            s.passwordConfirmError,
            s.currentPasswordError
        ).all { it == null }

        val confirmOk = if (passwordChanging) {
            s.confirmPassword.isNotBlank() && s.passwordConfirmError == null
        } else true

        val anyChange =
            s.nickname != originalUser?.nickname ||
                    s.email != originalUser?.email ||
                    passwordChanging ||
                    s.profilePictureUrl != initialProfilePicture

        val hasCurrentPassword = s.currentPassword.isNotBlank()

        _state.update { it.copy(canSubmit = noErrors && confirmOk && anyChange && hasCurrentPassword) }
    }



    fun submitChanges() {
        val s = _state.value
        if (!s.canSubmit || s.isSubmitting) return

        _state.update { it.copy(isSubmitting = true, errorMessage = null, success = false) }

        viewModelScope.launch {
            try {
                currentUserId?.let { id ->
                    val user = userRepository.getUserById(id)
                    if (user == null || user.pass != s.currentPassword) {
                        _state.update {
                            it.copy(
                                isSubmitting = false,
                                success = false,
                                currentPasswordError = "Contraseña actual incorrecta"
                            )
                        }
                        return@launch
                    }

                    if (s.nickname.isNotBlank() && s.nickname != originalUser?.nickname)
                        userRepository.updateNickname(id, s.nickname)

                    if (s.email.isNotBlank() && s.email != originalUser?.email)
                        userRepository.updateEmail(id, s.email)

                    if (s.password.isNotBlank())
                        userRepository.updatePassword(id, s.password)

                    val userUpdated = user.copy(
                        nickname = if (s.nickname != user.nickname) s.nickname else user.nickname,
                        email = if (s.email != user.email) s.email else user.email
                    )

                    originalUser = userUpdated

                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            success = true,
                            canSubmit = false,
                            password = "",
                            confirmPassword = "",
                            currentPassword = ""
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false, success = false, errorMessage = e.message) }
            }
        }
    }


    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clear()
            _state.value = EditProfileUiState()
            originalUser = null
            currentUserId = null
            initialProfilePicture = null
            onLogout()
        }
    }

    fun clearResult() {
        _state.update { it.copy(success = false, errorMessage = null) }
    }


}