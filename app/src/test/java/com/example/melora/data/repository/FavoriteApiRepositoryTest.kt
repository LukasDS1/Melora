package com.example.melora.data.repository

import com.example.melora.data.remote.FavoriteApi
import com.example.melora.data.remote.dto.SongDetailedDto
import io.mockk.MockK
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import retrofit2.Response
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals

class FavoriteApiRepositoryTest {

    @Test
    fun getByFavorite_returns_success() = runBlocking {
        val api = mockk<FavoriteApi>()
        val repo = FavoriteApiRepository(api)

        val userId = 1L
        val mockList = listOf(
            SongDetailedDto(
                idSong = 1L,
                songName = "Song 1",
                songDescription = "Desc",
                songDuration = 200,
                coverArt = null,
                songPathBase64 = null,
                creationDate = 123456L,
                artistId = 10L,
                nickname = "Artist",
                coverArtBase64 = null,
                audioBase64 = null
            )
        )

        coEvery { api.getFavorites(userId) } returns mockList

        val result = runCatching { repo.getByFavorite(userId) }

        assertTrue(result.isSuccess)
        assertEquals(mockList, result.getOrNull())
    }

    @Test
    fun isFavorite_returns_true() = runBlocking {
        val api = mockk<FavoriteApi>()
        val repo = FavoriteApiRepository(api)

        val userId = 1L
        val songId = 2L

        coEvery { api.isFavorite(userId, songId) } returns true

        val result = runCatching { repo.isFavorite(userId, songId) }

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
    }

    @Test
    fun toggleFavorite_returns_true() = runBlocking {
        val api = mockk<FavoriteApi>()
        val repo = FavoriteApiRepository(api)

        val userId = 1L
        val songId = 2L

        coEvery { api.toggleFavorite(userId, songId) } returns true

        val result = runCatching { repo.toggleFavorite(userId, songId) }

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
    }

    @Test
    fun deleteFavorite_completes_success() = runBlocking {
        val api = mockk<FavoriteApi>()
        val repo = FavoriteApiRepository(api)

        val userId = 1L
        val songId = 2L

        coEvery { api.deleteFavorite(userId, songId) } returns Unit

        val result = runCatching { repo.deleteFavorite(userId, songId) }

        assertTrue(result.isSuccess)
    }
}