package com.example.melora.domain.validation

import android.content.Context
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ValidatorsTest {

    private val context = mockk<Context>(relaxed = true)
     // ============ validacion de song ==============================
    @Test
    fun songValidation_nullUri_returnsError() {
        val error = songValidation(context, null)
        assertEquals("The song cant be empty", error)
    }

    //======================= Validacion de cover =======================
    @Test
    fun songCoverArtValidation_nullUri_returnsError() {
        val error = songCoverArtValidation(context, null)
        assertEquals("The song covert art cant be empty", error)
    }


    //===============Validacion de SongName =======================================================
    //Probar que el validador de nombres de cancione devuelva nulo
    @Test
    fun validateSongName_OK(){
        val error = songNameValidation("TestSong")
        assertNull(error)
    }

    //Probar que el validador de nombres de canciones devuelva el error
    @Test
    fun validateSongName_Error(){
        val error = songNameValidation(" ")

        assertEquals("The song name cant be empty",error)
    }

    //Probar que el validador de nombres de canciones devuelva error si contiene espacios en blancos
    @Test
    fun validateSongNameContainsSpace_Error(){
        val error = songNameValidation("Test Song")

        assertEquals("The song name cant contain spaces",error)
    }

    //================================================================================================

    //=========================Validacion Email ======================================================

    //Probar que la validacion sea correcta
    @Test
    fun validateEmail_OK(){
        val error = validateEmail("Test@Gmail.com")
        assertNull(error)
    }

    //Probar que la validacion devuelva error
    @Test
    fun validateEmailIsBlank(){
        val error = validateEmail("")

        assertEquals("El email es obligatorio.",error)
    }

    //Probar que la valicacion devuelva error por el formato
    @Test
    fun validateEmailHaveEmailFormat(){
        val error = validateEmail("a.com")

        assertEquals("Formato de email inválido",error)
    }
    //==============================================================================================

    //=================================Validacion de NickName=======================================
    //Probar que la validacion del nickname devuelve ok
    @Test
    fun validatNickName_OK(){
        val error = validateNickname("TestUser")
        assertNull(error)
    }

    //Probar que la validacion del nickname no puede estar vacía
    @Test
    fun validateNickNameIsBlank(){
        val error = validateNickname("")
        assertEquals("El nombre de usuario es obligatorio.",error)
    }
    //Probar que el nickname no contenga espacios
    @Test
    fun validateNicknameNotContainSpace(){
        val error = validateNickname("Test User")
        assertEquals("No debe contener espacios.",error)
    }

    //Probar que el nickname no contenga caracteres especiales
    @Test
    fun validateNickNameNotContainsSpecialChars(){
        val error = validateNickname("Test-@")
        assertEquals("Sólo letras, números y . _ -",error)
    }

    @Test
    fun validateLengthNickname(){
        val error = validateNickname("a")
        val error2 = validateNickname("aaaaaaaaaaaaaaaaaaaaa")
        assertEquals("Debe tener entre 3 a 20 caracteres",error)
        assertEquals("Debe tener entre 3 a 20 caracteres",error2)
    }

    //==============================================================================================

    //==============================Validaciones de Password =======================================

    //Probar que la password no este vacía
    @Test
    fun validatePassIsBlank(){
        val error = validatePassword("")
        assertEquals("La contraseña es obligatoria.",error)
    }

    //Probar la longitud de los password
    @Test
    fun validatePassLength(){
        val error = validatePassword("a")
        assertEquals("Mínimo 8 caracteres.",error)
    }

    //Probar que la password cumpla con mayuscula y minuscula
    @Test
    fun validatePassHaveUpperCaseAndLowerCase(){
        val error = validatePassword("strongpass2025@")
        val error2 = validatePassword("STRONGPASS2025@")

        assertEquals("Debe incluir al menos una mayúscula.",error)
        assertEquals("Debe incluir al menos una minúscula.",error2)
    }

    //Probar que la password cumpla con numeros y un caracter especial
    @Test
    fun validatePassContainsNumberAndSpecialChar(){
        val error = validatePassword("Strongpass@")
        assertEquals("Debe incluir al menos un número.",error)

        val error2  = validatePassword("Strongpass123")
        assertEquals("Debe incluir al menos un símbolo.",error2)
    }

    //Probar que la password no contenga espacios
    @Test
    fun validatePassNotContainsSpace(){
        val error = validatePassword("Strongpass12@ 1")
        assertEquals("No debe contener espacios.",error)
    }

    //Probar que la pass devuelva ok
    @Test
    fun validatePass_Ok(){
        val error = validatePassword("StrongPass2025@")
        assertNull(error)
    }
    //Probar que la confirmacion no este vacia
    @Test
    fun validatePassConfirmIsBlank(){
        val error = validateConfirmPassword("StrongPass2025@","")
        assertEquals("Confirma tu contraseña.",error)
    }

    //Probar que las contraseñas no son iguales
    @Test
    fun validatePassConfirmAreNotEquals(){
        val error = validateConfirmPassword("StrongPass2025@","StrongPass2026@")
        assertEquals("Las contraseñas no coinciden",error)
    }

    //Probar que la confirmacion este ok
    @Test
    fun validatePassConfirm_OK(){
        val error = validateConfirmPassword("StrongPass2025@","StrongPass2025@")
        assertNull(error)
    }
    //==============================================================================================







}