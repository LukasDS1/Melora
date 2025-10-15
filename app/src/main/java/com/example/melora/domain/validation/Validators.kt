package com.example.melora.domain.validation

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri

fun songNameValidation(songName:String):String?{
    return if(songName.isBlank()) "The song name cant be empty" else null
}
fun artistNameValidation(artistName:String):String?{
    return if(artistName.isBlank()) "The artist name cant be empty" else null
}

fun songValidation(context: Context,songUri:Uri?):String?{
    if(songUri == null) return  "The song cant be empty"
    return try {    //abrimos el arhcivo
        context.contentResolver.openInputStream(songUri)?.use { stream ->
            if(stream.available() <= 0) "the audio file is empty or invalid" else null //si el archivo tiene 0 bytes esque esta vacio
        }?: "cant open the audio file"
    } catch (e: Exception){
        "cant process the audio file"
    }

}

fun songCoverArtValidation(context: Context, songCoverArtUri: Uri?): String?{
    if(songCoverArtUri == null) return "The song covert art cant be empty"
    return try {
        //obtiene la imagen de la uri
        val stream = context.contentResolver.openInputStream(songCoverArtUri)
        //crear objeto de config bitmap y obtenemos solo el ancho y el alto
        val option = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeStream(stream,null,option)
        stream?.close()

        val w = option.outWidth
        val h = option.outHeight

        when {
            w <= 0 || h <= 0 -> "The image is not valid"
            w < 500 || h < 500 -> "The image must be at least 500x500 px"
            else -> null
        }
    } catch (e: Exception){
        "Cant be process the image"
    }

}