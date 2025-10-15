package com.example.melora.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val artistName : String = "",
    val songName: String = "",
    val songDescription: String? = null,
    val releaseDate: Date = Date(),
    val coverArt: Uri? =null, // tiene que empezar en null y lugo se cambia su estado //errores
    val song: Uri? = null,
    val artistNameError: String? = null,
    val songNameError: String? = null,
    val coverArtError: String? =null,
    val songError: String? = null,
    //banderas
    val isSubmitting: Boolean = false,                     // Flag de carga
    val canSubmit: Boolean = false,                        // Habilitar botón
    val success: Boolean = false,                          // Resultado OK
    val errorMsg: String? = null
)


//Modelo minimo de como deberia de ser subida la musica
private data class DemoMusicUpload(
    val artistName : String ,
    val songName: String ,
    val songDescription: String? ,
    val releaseDate: Date = Date(),
    val coverArt: Uri?,
    val song: Uri?
)

class UploadViewModel: ViewModel(){
    companion object{
        private val Music = mutableListOf(
            DemoMusicUpload("Test","Test1","Test1", Date(),null,null)
        )
    }

    //Flujos de estados
    private val _upload = MutableStateFlow(UploadUiState())
    val upload: StateFlow<UploadUiState> = _upload //pagina visible, solo lectura

    //funcion para habilitar el boton de enviar
    private fun recomputeUpdateCanSubmit(){
        val s = _upload.value
        val noErrors = listOf(s.songNameError,s.artistNameError,s.coverArtError).all { it == null }
        val filled = s.artistName.isNotBlank() && s.songName.isNotBlank() && s.coverArt != null && s.song != null
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

    fun onArtistNameChange(name: String) {
        _upload.update {
            it.copy(
                artistName = name,
                artistNameError = if (name.isBlank()) "The artist name cannot be empty" else null
            )
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
            Music.add(
                DemoMusicUpload(
                    artistName = s.artistName,
                    songName = s.songName,
                    songDescription = s.songDescription,
                    coverArt = s.coverArt,
                    song = s.song

                )
            )
            //exito
            _upload.update {
                it.copy(isSubmitting = false, success = true, errorMsg = null)
            }
        }
    }

    //funcion para limpiar banderas
    fun clearUpload(){
        _upload.update { it.copy(success = false, errorMsg = null) }
    }

}



