package com.example.melora.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melora.data.repository.SongRepository
import com.example.melora.data.repository.UploadRepository
import com.example.melora.domain.validation.songCoverArtValidation
import com.example.melora.domain.validation.songValidation
import kotlinx.coroutines.delay
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


class UploadViewModel( private  val repository: SongRepository): ViewModel(){
    //Flujos de estados
    private val _upload = MutableStateFlow(UploadUiState())
    val upload: StateFlow<UploadUiState> = _upload //pagina visible, solo lectura

    //funcion para habilitar el boton de enviar
    private fun recomputeUpdateCanSubmit(){
        val s = _upload.value
        val noErrors = listOf(s.songNameError,s.coverArtError).all { it == null }
        val filled =  s.songName.isNotBlank() && s.coverArt != null && s.song != null
        _upload.update { it.copy(canSubmit = noErrors && filled) }
    }
    fun onSongCoverChange(context: Context, value : Uri?){
        //actualizar estado
        _upload.update {
            it.copy(coverArt = value, coverArtError = songCoverArtValidation(context,value))
        }
        recomputeUpdateCanSubmit()
    }
    fun onSongChange(context: Context, value: Uri?) {
        //actualizar estado
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

    fun submitMusic(){
        val s = _upload.value
        if(!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _upload.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(700)

            val result = repository.uploadMusic(
                songName = s.songName,
                songPath = s.song,
                coverArt = s.coverArt,
                songDescription = s.songDescription,
                durationSong = 0,
                creationDate = Date().time
            )

            _upload.update {
                if(result.isSuccess){
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                }else{
                    it.copy(isSubmitting = false, success = false, errorMsg = result.exceptionOrNull()?.message?: "Cannot upload your Music :(")
                }
            }
        }
    }

    //funcion para limpiar banderas
    fun clearUpload(){
        _upload.update { it.copy(success = false, errorMsg = null) }
    }

}



