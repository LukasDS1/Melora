package com.example.melora.domain.validation

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Patterns

fun songNameValidation(songName:String):String?{
    if(songName.contains(' ')) return  "The song name cant contain spaces"
    return if(songName.trim().isBlank()) "The song name cant be empty" else null


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

fun validateEmail(email: String): String? {
    if (email.isBlank()) return "El email es obligatorio."
    val ok = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return if (!ok) "Formato de email inválido" else null
}

fun validateNickname(nickname: String): String? {
    if (nickname.isBlank()) return "El nombre de usuario es obligatorio."
    if (nickname.contains(' ')) return "No debe contener espacios."
    val regex = Regex("^[A-Za-z0-9._-]+$")
    if (!regex.matches(nickname)) return "Sólo letras, números y . _ -"
    if (nickname.length !in 3..20) return "Debe tener entre 3 a 20 caracteres"
    return null
}

fun validatePassword(pass: String): String? {
    if (pass.isBlank()) return "La contraseña es obligatoria."
    if (pass.length < 8) return "Mínimo 8 caracteres."
    if (!pass.any { it.isUpperCase() }) return "Debe incluir al menos una mayúscula."
    if (!pass.any { it.isLowerCase() }) return "Debe incluir al menos una minúscula."
    if (!pass.any { it.isDigit() }) return "Debe incluir al menos un número."
    if (!pass.any { it.isLetterOrDigit() }) return "Debe incluir al menos un símbolo."
    if (pass.contains(' ')) return "No debe contener espacios."
    return null
}

fun validateConfirmPassword(pass: String, confirm: String): String? {
    if (confirm.isBlank()) return "Confirma tu contraseña."
    return if (pass != confirm) "Las contraseñas no coinciden" else null
}


