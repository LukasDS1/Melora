    package com.example.melora.viewmodel

    import android.content.Context
    import android.media.MediaMetadataRetriever
    import android.net.Uri
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.melora.data.remote.dto.UploadMusicDto
    import com.example.melora.data.repository.UploadApiRepository
    import com.example.melora.domain.validation.songCoverArtValidation
    import com.example.melora.domain.validation.songNameValidation
    import com.example.melora.domain.validation.songValidation
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.update
    import kotlinx.coroutines.launch
    import java.io.InputStream
    import java.util.Base64

    data class UploadApiUiState(
        val songName: String = "",
        val songDescription: String? = null,

        val coverArtUri: Uri? = null,
        val songUri: Uri? = null,

        val coverArtBase64: String? = null,
        val songBase64: String? = null,

        val duration: Int = 0,

        val songNameError: String? = null,
        val coverArtError: String? = null,
        val songError: String? = null,

        val canSubmit: Boolean = false,
        val isSubmitting: Boolean = false,
        val success: Boolean = false,
        val errorMessage: String? = null
    )


    class UploadApiViewModel(
        private val repository: UploadApiRepository = UploadApiRepository()
    ) : ViewModel() {

        private val _uiState = MutableStateFlow(UploadApiUiState())
        val uiState: StateFlow<UploadApiUiState> = _uiState

        // ---------------------------------------------------------
        // Handlers
        // ---------------------------------------------------------

        fun onSongNameChange(value: String) {
            _uiState.update {
                it.copy(
                    songName = value,
                    songNameError = songNameValidation(value)
                )
            }
            recomputeCanSubmit()
        }

        fun onSongDescriptionChange(value: String) {
            _uiState.update { it.copy(songDescription = value.ifBlank { null }) }
        }

        fun onCoverArtChange(context: Context, uri: Uri?) {
            _uiState.update {
                it.copy(
                    coverArtUri = uri,
                    coverArtError = songCoverArtValidation(context, uri),
                    coverArtBase64 = uri?.let { encodeUriToBase64(context, it) }
                )
            }
            recomputeCanSubmit()
        }

        fun onSongFileChange(context: Context, uri: Uri?) {
            _uiState.update {
                it.copy(
                    songUri = uri,
                    songError = songValidation(context, uri),
                    songBase64 = uri?.let { encodeUriToBase64(context, it) },
                    duration = getSongDuration(context, uri)
                )
            }
            recomputeCanSubmit()
        }

        // ---------------------------------------------------------
        // Helpers
        // ---------------------------------------------------------

        private fun encodeUriToBase64(context: Context, uri: Uri): String? {
            return try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                Base64.getEncoder().encodeToString(bytes)
            } catch (e: Exception) {
                null
            }
        }

        private fun getSongDuration(context: Context, uri: Uri?): Int {
            if (uri == null) return 0
            val retriever = MediaMetadataRetriever()

            return try {
                retriever.setDataSource(context, uri)
                val durationMs =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
                (durationMs / 1000).toInt()
            } catch (_: Exception) {
                0
            } finally {
                retriever.release()
            }
        }

        private fun recomputeCanSubmit() {
            val s = _uiState.value
            val can =
                s.songNameError == null &&
                        s.coverArtError == null &&
                        s.songError == null &&
                        s.songName.isNotBlank() &&
                        s.coverArtUri != null &&
                        s.songUri != null

            _uiState.update { it.copy(canSubmit = can) }
        }

        // ---------------------------------------------------------
        // SUBMIT → Microservicio upload
        // ---------------------------------------------------------

        fun submitUpload(userId: Long) {
            val s = _uiState.value
            if (!s.canSubmit || s.isSubmitting) return

            val dto = UploadMusicDto(
                userId = userId,
                songName = s.songName,
                songDescription = s.songDescription,
                songPathBase64 = s.songBase64,
                coverArt = s.coverArtBase64,
                songDuration = s.duration,
                creationDate = System.currentTimeMillis()
            )

            viewModelScope.launch {
                _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

                val result = repository.uploadSong(dto)

                if (result.isSuccess) {
                    _uiState.update { it.copy(isSubmitting = false, success = true) }
                } else {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = result.exceptionOrNull()?.message ?: "Upload failed"
                        )
                    }
                }
            }
        }

        fun clearState() {
            _uiState.value = UploadApiUiState()
        }
    }
