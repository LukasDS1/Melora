package com.example.melora.data.repository

import com.example.melora.data.remote.BanApi
import com.example.melora.data.remote.dto.BanRequestDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import retrofit2.Response

class BanApiRepositoryTest {

    //Test Para banear una cancion devuelva succes
    @Test
    fun BanApiRetunrs_ok() = runBlocking {
        val api = mockk<BanApi>()

        val repo = BanApiRepository(api)

        val sample = BanRequestDto("Test")

        val response = Response.success(Unit)

        coEvery { api.banSong(1L,sample) } returns response

        val result = repo.banSong(1L,"Test")

        assertTrue(result.isSuccess)
        assertEquals(Unit,result.getOrNull())
    }

    //Test para banear una cancion devuelva fail
    @Test
    fun BanApiReturns_fail() = runBlocking {
        val api = mockk<BanApi>()
        val repo = BanApiRepository(api)

        val sample = BanRequestDto("Test")

        val responseError = Response.error<Unit>(
            400,
            "Ban failed".toResponseBody()
        )

        coEvery { api.banSong(1L, sample) } returns responseError

        val result = repo.banSong(1L, "Test")

        assertTrue(result.isFailure)
    }






}