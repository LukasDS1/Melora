package com.example.melora.data.repository

import com.example.melora.data.remote.PlayListApi
import com.example.melora.data.remote.dto.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import retrofit2.Response

class PlaylistApiRepositoryTest {

    @Test
    fun createPlaylist_returns_success() = runBlocking {
        val api = mockk<PlayListApi>()
        val repo = PlaylistApiRepository(api)

        val dto = PlayListRequestDto(
            playlistName = "Test Playlist",
            userId = 10L,
            categoriaId = 1L,
            accesoId = 1L,
            songIds = null
        )
        val playlistDto = PlaylistDto(1L, "Test Playlist", 10L, "2025-11-23T19:00:00")
        coEvery { api.createPlaylist(dto) } returns Response.success(playlistDto)

        val result = repo.createPlaylist("Test Playlist", 10L, 1L, 1L, emptyList())

        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull())
    }

    @Test
    fun createPlaylist_returns_fail() = runBlocking {
        val api = mockk<PlayListApi>()
        val repo = PlaylistApiRepository(api)

        val dto = PlayListRequestDto(
            playlistName = "Test Playlist",
            userId = 10L,
            categoriaId = 1L,
            accesoId = 1L,
            songIds = null
        )
        coEvery { api.createPlaylist(dto) } returns Response.error(
            400,
            "Bad request".toResponseBody()
        )

        val result = repo.createPlaylist("Test Playlist", 10L, 1L, 1L, emptyList())

        assertTrue(result.isFailure)
    }

    @Test
    fun getAllPlaylists_returns_list() = runBlocking {
        val api = mockk<PlayListApi>()
        val repo = PlaylistApiRepository(api)

        val playlists = listOf(
            PlaylistDto(1L, "P1", 1L, "2025-11-23T19:00:00"),
            PlaylistDto(2L, "P2", 2L, "2025-11-22T19:00:00")
        )

        coEvery { api.getAllPlaylists() } returns playlists

        val result = repo.getAllPlaylists()

        assertEquals(2, result.size)
        assertEquals("P1", result[0].playlistName)
    }

    @Test
    fun getPlaylistById_returns_playlist() = runBlocking {
        val api = mockk<PlayListApi>()
        val repo = PlaylistApiRepository(api)

        val playlist = PlaylistDto(1L, "P1", 1L, "2025-11-23T19:00:00")
        coEvery { api.getPlaylistById(1L) } returns playlist

        val result = repo.getPlaylistById(1L)

        assertEquals("P1", result?.playlistName)
    }

    @Test
    fun getSongsFromPlaylist_returns_songs() = runBlocking {
        val api = mockk<PlayListApi>()
        val repo = PlaylistApiRepository(api)

        val songs = listOf(
            PlaylistSongDto(1L, "Song1", null, null, null, 200, 123456L, "Artist1", 10L, null, null),
            PlaylistSongDto(2L, "Song2", null, null, null, 180, 123457L, "Artist2", 11L, null, null)
        )

        coEvery { api.getSongsFromPlaylist(1L) } returns songs

        val result = repo.getSongsFromPlaylist(1L)

        assertEquals(2, result.size)
        assertEquals("Song1", result[0].songName)
    }

    @Test
    fun getPlaylistsByUser_returns_list() = runBlocking {
        val api = mockk<PlayListApi>()
        val repo = PlaylistApiRepository(api)

        val playlists = listOf(
            PlaylistDto(1L, "P1", 1L, "2025-11-23T19:00:00"),
            PlaylistDto(2L, "P2", 1L, "2025-11-22T19:00:00")
        )

        coEvery { api.getPlaylistsByUser(1L) } returns playlists

        val result = repo.getPlaylistsByUser(1L)

        assertEquals(2, result.size)
        assertEquals("P1", result[0].playlistName)
    }

    @Test
    fun searchPlaylistsByName_returns_list() = runBlocking {
        val api = mockk<PlayListApi>()
        val repo = PlaylistApiRepository(api)

        val playlists = listOf(
            PlaylistDto(1L, "Rock Hits", 1L, "2025-11-23T19:00:00")
        )

        coEvery { api.searchPlaylistsByName("Rock") } returns playlists

        val result = repo.searchPlaylistsByName("Rock")

        assertEquals(1, result.size)
        assertEquals("Rock Hits", result[0].playlistName)
    }
}
