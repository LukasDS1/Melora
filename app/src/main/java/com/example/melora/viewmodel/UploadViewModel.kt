package com.example.melora.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.repository.SongRepository
import com.example.melora.data.repository.UploadRepository
import com.example.melora.domain.validation.songCoverArtValidation
import com.example.melora.domain.validation.songValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class  UploadUiState(
    //datos de cancion
    val songName: String = "",
    val songDescription: String? = null,
    val releaseDate: Date = Date(),
    val coverArt: Uri? =null, // tiene que empezar en null y lugo se cambia su estado //errores
    val song: Uri? = null,
    val durationSong: Int = 0,
    val songNameError: String? = null,
    val coverArtError: String? =null,
    val songError: String? = null,
    //banderas
    val isSubmitting: Boolean = false,                     // Flag de carga
    val canSubmit: Boolean = false,                        // Habilitar botón
    val success: Boolean = false,                          // Resultado OK
    val errorMsg: String? = null
)

class UploadViewModel( private  val repository: SongRepository,private val uploadRepository: UploadRepository,): ViewModel() {
    //Flujos de estados
    private val _upload = MutableStateFlow(UploadUiState())
    val upload: StateFlow<UploadUiState> = _upload //pagina visible, solo lectura

    //funcion para habilitar el boton de enviar
    private fun recomputeUpdateCanSubmit() {
        val s = _upload.value
        val noErrors = listOf(s.songNameError, s.coverArtError).all { it == null }
        val filled = s.songName.isNotBlank() && s.coverArt != null && s.song != null
        _upload.update { it.copy(canSubmit = noErrors && filled) }
    }
    fun onSongCoverChange(context: Context, value: Uri?) {
        //actualizar estado
        _upload.update {
            it.copy(coverArt = value, coverArtError = songCoverArtValidation(context, value))
        }
        recomputeUpdateCanSubmit()
    }
    fun onSongChange(context: Context, value: Uri?) {
        _upload.update {
            it.copy(song = value, songError = songValidation(context, value))
        }
        recomputeUpdateCanSubmit()
    }
    fun onSongNameChange(name: String) {
        _upload.update {
            it.copy(
                songName = name,
                songNameError = if (name.isBlank()) "The Song name cannot be empty" else null
            )
        }
        recomputeUpdateCanSubmit()
    }
    fun onSongDescriptionChange(value: String) {
        _upload.update { it.copy(songDescription = value.ifBlank { null }) }
    }

    fun submitMusic(userId: Long) {
        val s = _upload.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _upload.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            val songResult = repository.uploadMusic(
                songName = s.songName,
                songPath = s.song,
                coverArt = s.coverArt,
                songDescription = s.songDescription,
                durationSong = 0,
                creationDate = s.releaseDate.time
            )

            if (songResult.isSuccess) {
                val songId = songResult.getOrNull()!!
                val uploadResult = uploadRepository.insertUpload(
                    userID = userId,
                    songID = songId,
                    stateId = 1
                )

                _upload.update {
                    if (uploadResult.isSuccess)
                        it.copy(isSubmitting = false, success = true)
                    else
                        it.copy(isSubmitting = false, errorMsg = "Cannot upload Music")
                }
            } else {
                _upload.update { it.copy(isSubmitting = false, errorMsg = "Cannot upload Music") }
            }
        }
    }

    fun clearForm(){
        _upload.value = UploadUiState()
    }
    fun clearUpload(){
        _upload.update { it.copy(errorMsg = null) }
    }

}






