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
}