package com.example.melora.data.repository

import com.example.melora.data.remote.SongApi
import com.example.melora.data.remote.SongRemoteModule
import com.example.melora.data.remote.dto.SongDetailedDto

class SongApiRepository(
    private val api: SongApi = SongRemoteModule.api()
) {

    // -----------------------------------------
    // GET ALL SONGS
    // -----------------------------------------
    suspend fun getAllSongs(): Result<List<SongDetailedDto>> = try {
        val list = api.getAll()
        Result.success(list)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // -----------------------------------------
    // GET BY ID
    // -----------------------------------------
    suspend fun getSongById(id: Long): Result<SongDetailedDto> = try {
        Result.success(api.getById(id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // -----------------------------------------
    // GET BY ARTIST
    // -----------------------------------------
    suspend fun getSongsByArtist(id: Long): Result<List<SongDetailedDto>> = try {
        Result.success(api.getByArtist(id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // -----------------------------------------
    // SEARCH
    // -----------------------------------------
    suspend fun search(q: String): Result<List<SongDetailedDto>> = try {
        Result.success(api.search(q))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // -----------------------------------------
    // PATCH
    // -----------------------------------------
    suspend fun changeSongDetails(id: Long, name: String?, desc: String?): Result<Unit> = try {

        val body = mutableMapOf<String, String?>()

        if (name != null) body["songName"] = name
        if (desc != null) body["songDescription"] = desc

        api.patchSong(id, body)
        Result.success(Unit)

    } catch (e: Exception) {
        Result.failure(e)
    }


    // -----------------------------------------
    // DELETE
    // -----------------------------------------
    suspend fun deleteSong(id: Long): Result<Unit> = try {
        api.deleteSong(id)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
