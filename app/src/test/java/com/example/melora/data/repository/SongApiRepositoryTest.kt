package com.example.melora.data.repository

import com.example.melora.data.remote.SongApi
import com.example.melora.data.remote.dto.SongDetailedDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.Response
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals

class SongApiRepositoryTest {

    //Test para saber que getByIdSong devuelve el dto
    @Test
   fun songRepositoryGetById_ReturnsOk() = runBlocking {

       val api = mockk<SongApi>()

       val repo = SongApiRepository(api)

       val sample = SongDetailedDto(1,"Test",null,100,null,
           null,100L,1L,"Test",null,null)

        coEvery { api.getById(sample.idSong) }  returns sample

        val result = repo.getSongById(sample.idSong)

        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull()!!.idSong)

    }

    // Test para saber que getByIdSong devuelve fail
    @Test
    fun songRepositoryGetById_ReturnsFail() = runBlocking {

        val api = mockk<SongApi>()
        val repo = SongApiRepository(api)

        coEvery { api.getById(1L) } throws Exception("Canción no encontrada")

        val result = repo.getSongById(1L)

        assertTrue(result.isFailure)
        assertEquals("Canción no encontrada", result.exceptionOrNull()!!.message)
    }

    //Test para obtener todos los usuarios
    @Test
    fun getAllSongs_ReturnsSuccess() = runBlocking {
        val api = mockk<SongApi>()
        val repo = SongApiRepository(api)

        val sample = listOf(
            SongDetailedDto(1,"Test",null,
                100,null,null,100L,1L,"Test",null,
                null)
        )

        coEvery { api.getAll() } returns sample

        val result = repo.getAllSongs()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
    }

    //Test para saber que getAllSongs Falla
    @Test
    fun getAllSongs_ReturnsFail() = runBlocking {
        val api = mockk<SongApi>()
        val repo = SongApiRepository(api)

        coEvery { api.getAll() } throws Exception("Error al obtener canciones")

        val result = repo.getAllSongs()

        assertTrue(result.isFailure)
        assertEquals("Error al obtener canciones", result.exceptionOrNull()!!.message)
    }

    //Test para saber que getSongByArtist return succes
    @Test
    fun getSongsByArtist_ReturnsSuccess() = runBlocking {
        val api = mockk<SongApi>()
        val repo = SongApiRepository(api)

        val songs = listOf(
            SongDetailedDto(1,
                "Test",null,100,null,null,100L,5L,"Test",null,
                null)
        )

        coEvery { api.getByArtist(5L) } returns songs

        val result = repo.getSongsByArtist(5L)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
    }

    //Test de getSongByArtist devuelva error
    @Test
    fun getSongsByArtist_ReturnsFail() = runBlocking {
        val api = mockk<SongApi>()
        val repo = SongApiRepository(api)

        coEvery { api.getByArtist(5L) } throws Exception("Artista no encontrado")

        val result = repo.getSongsByArtist(5L)

        assertTrue(result.isFailure)
        assertEquals("Artista no encontrado", result.exceptionOrNull()!!.message)
    }

    //Test para saber que update devuelve succes
    @Test
    fun updateSongDetails_ReturnsSuccess() = runBlocking {
        val api = mockk<SongApi>()
        val repo = SongApiRepository(api)

        coEvery { api.patchSong(1L, any()) } returns Response.success(Unit)

        val result = repo.changeSongDetails(1L, "Nuevo nombre", "Nueva desc")

        assertTrue(result.isSuccess)
    }

    //Test para saber que deletesong Devuelve succes
    @Test
    fun deleteSong_ReturnsSuccess() = runBlocking {
        val api = mockk<SongApi>()
        val repo = SongApiRepository(api)

        coEvery { api.deleteSong(1L) } returns Response.success(Unit)

        val result = repo.deleteSong(1L)

        assertTrue(result.isSuccess)
    }

    //Test para buscar canciones
    @Test
    fun searchSongs_ReturnsSuccess() = runBlocking {
        val api = mockk<SongApi>()
        val repo = SongApiRepository(api)

        val sample = listOf(
            SongDetailedDto(1,"SearchTest",null
                ,120,null,null,100L,1,"Tester",null,null)
        )

        coEvery { api.search("Test") } returns sample

        val result = repo.search("Test")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
    }
    //Test buscar canciones falla
    @Test
    fun searchSongs_ReturnsFail() = runBlocking {
        val api = mockk<SongApi>()
        val repo = SongApiRepository(api)

        coEvery { api.search("Test") } throws Exception("Error de búsqueda")

        val result = repo.search("Test")

        assertTrue(result.isFailure)
        assertEquals("Error de búsqueda", result.exceptionOrNull()!!.message)
    }
}