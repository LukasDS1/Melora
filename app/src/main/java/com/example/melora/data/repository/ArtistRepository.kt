package com.example.melora.data.repository

import com.example.melora.data.local.song.SongDao
import com.example.melora.data.local.song.SongDetailed
import com.example.melora.data.local.users.UserDao
import com.example.melora.data.local.users.UserEntity

data class ArtistProfilData(
    val artist: UserEntity,
    val songs: List<SongDetailed>
)

class ArtistRepository(
    private val userDao: UserDao,
    private val songDao: SongDao
){
    suspend fun getSongByArtist(artistId: Long): ArtistProfilData?{
        val artist = userDao.getById(artistId)?: return null
        val songs = songDao.getSongsForArtist(artistId)
        return ArtistProfilData(artist,songs)
    }
}