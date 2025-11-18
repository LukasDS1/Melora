package com.example.melora.data.repository

import com.example.melora.data.remote.SongApi
import com.example.melora.data.remote.dto.ArtistProfileData
import com.example.melora.data.storage.UserPreferences
import kotlinx.coroutines.flow.first

class ArtistRepository(
    private val musicApi: SongApi,
    private val prefs: UserPreferences
) {

    suspend fun getMyProfile(): ArtistProfileData {

        val idUser = prefs.userId.first() ?: error("User not logged")
        val nickname = prefs.nickname.first() ?: "Unknown"
        val email = prefs.email.first() ?: "Unknown"
        val roleId = prefs.userRoleId.first() ?: 1L
        val photo = prefs.profilePicture.first()

        val songs = musicApi.getByArtist(idUser)

        return ArtistProfileData(
            idUser = idUser,
            nickname = nickname,
            email = email,
            roleId = roleId,
            profilePhotoBase64 = photo,
            songs = songs
        )
    }
}
