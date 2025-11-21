package com.example.melora.data.repository

import com.example.melora.data.remote.UploadApi
import com.example.melora.data.remote.dto.UploadMusicDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.Response
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
class UploadApiRepositoryTest {

    //Test unitario hecho para cuando se haga un upload retorne Ok
    @Test
    fun UploadRepository_Upload_returns_ok() = runBlocking {

        val api = mockk<UploadApi>()
        val repo = UploadApiRepository(api)

        val sample = UploadMusicDto(1,"Test",null,null,null,100,10L)

        val response = Response.success(Any())

        coEvery { api.uploadSong(sample) } returns response

        val result = repo.uploadSong(sample)
        assertTrue(result.isSuccess)
    }

    //Test unitario hecho para ver como falla
    @Test
    fun UploadRepository_Upload_returns_isFailure() = runBlocking {
        val api = mockk<UploadApi>()
        val repo = UploadApiRepository(api)

        val sample = UploadMusicDto(1,"Test",null,null,null,100,10L)

        val response = Response.error<Any>(
            404,"Error al subir la cancion".toResponseBody()
        )

        coEvery { api.uploadSong(sample) } returns response

        val result = repo.uploadSong(sample)
        assertTrue(result.isFailure)
        assertEquals("Error al subir la cancion",result.getOrNull())
    }





}