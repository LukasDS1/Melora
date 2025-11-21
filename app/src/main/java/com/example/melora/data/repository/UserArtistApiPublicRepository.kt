package com.example.melora.data.repository

import com.example.melora.data.remote.SongApi
import com.example.melora.data.remote.dto.ArtistProfileData

class UserArtistApiPublicRepository(
    private val songApi: SongApi,
    private val registerApiRepository: RegisterApiRepository
) {

    suspend fun getArtistProfile(artistId: Long): ArtistProfileData {
        val raw = registerApiRepository.getUserAsMap(artistId)

        val nickname = raw["nickname"] as String
        val email = raw["email"] as String
        val roleId = ((raw["rol"] as Map<*, *>)["idRol"] as Double).toLong()
        val photo = raw["profilePhotoUrl"] as? String

        val songs = songApi.getByArtist(artistId)

        return ArtistProfileData(
            idUser = artistId,
            nickname = nickname,
            email = email,
            roleId = roleId,
            profilePhotoBase64 = photo,
            songs = songs
        )
    }
}