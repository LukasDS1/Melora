package com.example.melora.data.repository

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.melora.data.local.song.SongDao
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.local.song.SongEntity
import com.example.melora.data.local.users.UserEntity

class SongRepository(
    private val songDao: SongDao
) {

    fun getDurationInSeconds(context: Context, songUri: Uri): Int {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, songUri)
        val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        retriever.release()
        return (durationMs / 1000).toInt()
    }

    suspend fun uploadMusic(
        context: Context,
        songName: String,
        songPath: Uri?,
        coverArt: Uri?,
        songDescription: String?,
        creationDate: Long
    ): Result<Long> {
        return try {
            if (songName.isBlank() || songPath == null) {
                return Result.failure(IllegalArgumentException("Name or Song file are invalid"))
            }

            val songDuration = getDurationInSeconds(context, songPath)

            val songEntity = SongEntity(
                songName = songName,
                songDescription = songDescription,
                songPath = songPath.toString(),
                durationSong = songDuration,
                coverArt = coverArt?.toString(),
                creationDate = creationDate
            )

            val id = songDao.insert(songEntity)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSong(songId: Long): Result<Unit> {
        return try {
            songDao.deleteById(songId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun playSongByID(songId: Long): SongDetailed =
        songDao.getSongByID(songId)

    suspend fun getSong(query: String): Result<List<SongDetailed>> =
        try {
            Result.success(songDao.getSong(query))
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun getAllSongs(): List<SongDetailed> = songDao.getAllSong()

    suspend fun getSongsForArtist(id: Long): Result<List<SongDetailed>> =
        try {
            Result.success(songDao.getSongsForArtist(id))
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun getArtistById(artistId: Long): UserEntity? =
        songDao.getUserById(artistId)
}