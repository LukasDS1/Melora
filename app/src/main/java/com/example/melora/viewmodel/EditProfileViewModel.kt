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
    val password: String = "",
    val passwordError: String? = null,
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
            currentUserId = userPreferences.userId.firstOrNull() ?: return@launch
            val user = userRepository.getUserById(currentUserId)
            Log.d("EditProfileVM", "Loaded user: $user")
            user?.let {
                originalUser = it
                initialProfilePicture = it.profilePicture
                _state.value = EditProfileUiState(
                    nickname = it.nickname,
                    email = it.email,
                    profilePictureUrl = it.profilePicture
                )
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

    private fun recomputeCanSubmit() {
        val s = _state.value
        val noErrors = listOf(s.nicknameError, s.emailError, s.passwordError).all { it == null }

        val anyChange =
            s.nickname != originalUser?.nickname ||
            s.email != originalUser?.email ||
            s.password.isNotBlank() ||
            s.profilePictureUrl != initialProfilePicture

        _state.update { it.copy(canSubmit = noErrors && anyChange) }
    }
    fun submitChanges() {
        val s = _state.value
        if (!s.canSubmit || s.isSubmitting) return

        _state.update { it.copy(isSubmitting = true, errorMessage = null, success = false) }

        viewModelScope.launch {
            try {
                currentUserId?.let { id ->
                    if (s.nickname.isNotBlank() && s.nickname != originalUser?.nickname)
                        userRepository.updateNickname(id, s.nickname)

                    if (s.email.isNotBlank() && s.email != originalUser?.email)
                        userRepository.updateEmail(id, s.email)

                    if (s.password.isNotBlank())
                        userRepository.updatePassword(id, s.password)
                }

                val user = originalUser
                if (user != null) {
                    originalUser = user.copy(
                        nickname = if (s.nickname != user.nickname) s.nickname else user.nickname,
                        email = if (s.email != user.email) s.email else user.email
                    )
                }



                _state.update { it.copy(isSubmitting = false, success = true, canSubmit = false, password = "") }
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false, success = false, errorMessage = e.message) }
            }
        }
    }

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clear()
            onLogout()
        }
    }

    fun clearResult() {
        _state.update { it.copy(success = false, errorMessage = null) }
    }


}