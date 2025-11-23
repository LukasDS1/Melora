package com.example.melora.data.repository

import com.example.melora.data.remote.RecoverPassApi
import com.example.melora.data.remote.dto.RecoverPassDto
import com.example.melora.data.remote.dto.RecoverPassResponse
import com.example.melora.data.remote.dto.ResetPasswordDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import retrofit2.Response

class RecoverPassApiRepositoryTest {

    @Test
    fun recoverPassword_returns_success() = runBlocking {
        val api = mockk<RecoverPassApi>()
        val repo = RecoverPassApiRepository(api)

        val responseDto = RecoverPassResponse("Email sent", true)
        coEvery { api.recoverPassword(RecoverPassDto("test@example.com")) } returns Response.success(responseDto)

        val result = repo.recoverPassword("test@example.com")

        assertTrue(result.isSuccessful)
        assertEquals(true, result.body()?.status)
        assertEquals("Email sent", result.body()?.message)
    }

    @Test
    fun recoverPassword_returns_fail() = runBlocking {
        val api = mockk<RecoverPassApi>()
        val repo = RecoverPassApiRepository(api)

        coEvery { api.recoverPassword(RecoverPassDto("test@example.com")) } returns Response.error(
            400,
            "Bad request".toResponseBody()
        )

        val result = repo.recoverPassword("test@example.com")

        assertTrue(!result.isSuccessful)
    }

    @Test
    fun resetPassword_returns_success() = runBlocking {
        val api = mockk<RecoverPassApi>()
        val repo = RecoverPassApiRepository(api)

        val responseDto = RecoverPassResponse("Password reset", true)
        coEvery { api.resetPassword("token123", ResetPasswordDto("newPass")) } returns Response.success(responseDto)

        val result = repo.resetPassword("token123", "newPass")

        assertTrue(result.isSuccessful)
        assertEquals(true, result.body()?.status)
        assertEquals("Password reset", result.body()?.message)
    }

    @Test
    fun validateToken_returns_success() = runBlocking {
        val api = mockk<RecoverPassApi>()
        val repo = RecoverPassApiRepository(api)

        val responseBody = "Valid token".toResponseBody()
        coEvery { api.validateToken("token123") } returns Response.success(responseBody)

        val result = repo.validateToken("token123")

        assertTrue(result.isSuccessful)
        assertEquals("Valid token", result.body()?.string())
    }

    @Test
    fun validateToken_returns_fail() = runBlocking {
        val api = mockk<RecoverPassApi>()
        val repo = RecoverPassApiRepository(api)

        coEvery { api.validateToken("token123") } returns Response.error(
            401,
            "Invalid token".toResponseBody()
        )

        val result = repo.validateToken("token123")

        assertTrue(!result.isSuccessful)
    }
}
