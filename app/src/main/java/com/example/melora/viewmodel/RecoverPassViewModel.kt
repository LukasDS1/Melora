package com.example.melora.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.remote.RecoverPassRemoteModule
import com.example.melora.data.remote.dto.RecoverPassDto
import com.example.melora.data.repository.RecoverPassApiRepository
import com.example.melora.domain.validation.validateEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecoverPassUiState(
    val email: String = "",
    val emailError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,

    val success: Boolean = false,
    val errorMessage: String? = null
)

class RecoverPassViewModel(
    private val repository: RecoverPassApiRepository
): ViewModel()  {


    private val _uiState = MutableStateFlow(RecoverPassUiState())
    val uiState: StateFlow<RecoverPassUiState> = _uiState

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(
                email = value,
                emailError = validateEmail(value)
            )
        }
        recomputeCanSubmit()
    }

    private fun recomputeCanSubmit() {
        val s = _uiState.value
        val can = s.emailError == null && s.email.isNotBlank()

        _uiState.update { it.copy(canSubmit = can) }
    }

    fun submit() {
        val s = _uiState.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            try {
                val response = repository.recoverPassword(email = s.email.trim())

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            success = body.status,
                            errorMessage = if (body.status) null else body.message
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            success = false,
                            errorMessage = "Error del servidor"
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMessage = "Error de red: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearResult() {
        _uiState.update { RecoverPassUiState() }
    }
}