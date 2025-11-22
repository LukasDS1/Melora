package com.example.melora.data.repository

import com.example.melora.data.remote.LoginApi
import com.example.melora.data.remote.dto.LoginData
import com.example.melora.data.remote.dto.LoginResponse
import com.example.melora.data.remote.dto.LoginUserDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals

class LoginApiRepositoryTest {

    //Prueba para saber que el login funciona
    @Test
    fun loginUser_returns_ok() = runBlocking {
        val api = mockk<LoginApi>()
        val repo = LoginApiRepository(api)

        val dto = LoginUserDto("test@gmail.com", "12345")

        val loginData = LoginData(
            idUser = 1L,
            email = "test@gmail.com",
            nickname = "TestUser",
            rolId = 1L,
            rolName = "ADMIN",
            profilePhotoBase64 = null
        )

        val loginResponse = LoginResponse(
            self = "link",
            data = loginData
        )

        val responseOK = Response.success(loginResponse)

        coEvery { api.login(dto) } returns responseOK

        val result = repo.loginUser(dto)

        assertTrue(result.isSuccess)
        assertEquals("test@gmail.com", result.getOrNull()!!.data!!.email)
    }

    //Prueba que comprueba el fail en el login
    @Test
    fun loginUser_returns_fail() = runBlocking {
        val api = mockk<LoginApi>()
        val repo = LoginApiRepository(api)

        val dto = LoginUserDto("test@gmail.com", "12345")

        val errorResponse = Response.error<LoginResponse>(
            400,
            "Credenciales incorrectas".toResponseBody()
        )

        coEvery { api.login(dto) } returns errorResponse

        val result = repo.loginUser(dto)

        assertTrue(result.isFailure)
        assertEquals("Credenciales incorrectas", result.exceptionOrNull()!!.message)
    }

}