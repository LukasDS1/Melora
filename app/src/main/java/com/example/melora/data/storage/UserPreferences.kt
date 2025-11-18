package com.example.melora.data.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    private val isLoggedInKey = booleanPreferencesKey("is_logged_in")
    private val userIdKey = longPreferencesKey("user_id")
    private val roleIdKey = longPreferencesKey("role_id")
    private val profilePictureKey = stringPreferencesKey("profile_picture_url")

    private val nicknameKey = stringPreferencesKey("nickname")
    private val emailKey = stringPreferencesKey("email")

    suspend fun saveLoginState(isLoggedIn: Boolean, userId: Long?, roleId: Long?) {
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = isLoggedIn

            if (userId != null) prefs[userIdKey] = userId
            else prefs.remove(userIdKey)

            if (roleId != null) prefs[roleIdKey] = roleId
            else prefs.remove(roleIdKey)
        }
    }

    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = value
        }
    }

    suspend fun setProfilePicture(uri: String?) {
        context.dataStore.edit { prefs ->
            if (uri != null) prefs[profilePictureKey] = uri
            else prefs.remove(profilePictureKey)
        }
    }

    suspend fun setNickname(value: String) {
        context.dataStore.edit { prefs ->
            prefs[nicknameKey] = value
        }
    }

    suspend fun setEmail(value: String) {
        context.dataStore.edit { prefs ->
            prefs[emailKey] = value
        }
    }

    val nickname: Flow<String?> = context.dataStore.data
        .map { it[nicknameKey] }

    val email: Flow<String?> = context.dataStore.data
        .map { it[emailKey] }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[isLoggedInKey] ?: false }

    val userId: Flow<Long?> = context.dataStore.data
        .map { prefs -> prefs[userIdKey] }

    val userRoleId: Flow<Long?> = context.dataStore.data
        .map { prefs -> prefs[roleIdKey] }

    val profilePicture: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[profilePictureKey] }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    suspend fun saveUserData(
        idUser: Long,
        roleId: Long,
        nickname: String,
        email: String,
        profilePhotoBase64: String?
    ) {
        context.dataStore.edit { prefs ->
            prefs[userIdKey] = idUser
            prefs[roleIdKey] = roleId
            prefs[nicknameKey] = nickname
            prefs[emailKey] = email

            if (profilePhotoBase64 != null)
                prefs[profilePictureKey] = profilePhotoBase64
            else
                prefs.remove(profilePictureKey)

            prefs[isLoggedInKey] = true
        }
    }
}
