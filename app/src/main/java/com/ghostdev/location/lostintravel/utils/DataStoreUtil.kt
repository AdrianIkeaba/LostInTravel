package com.ghostdev.location.lostintravel.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ghostdev.location.RecommendedPlacesQuery
import com.ghostdev.location.lostintravel.data.models.SerializablePlace
import com.ghostdev.location.lostintravel.data.models.toApolloModel
import com.ghostdev.location.lostintravel.data.models.toSerializable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

object PreferencesKeys {
    val API_TOKEN = stringPreferencesKey("api_token")
    val FULL_NAME = stringPreferencesKey("full_name")
    val PLACES = stringPreferencesKey("places")
}

// STORE FUNCTIONS
suspend fun DataStore<Preferences>.saveApiToken(token: String) {
    edit { preferences ->
        preferences[PreferencesKeys.API_TOKEN] = token
    }
}

suspend fun DataStore<Preferences>.saveFullName(fullName: String) {
    edit { preferences ->
        preferences[PreferencesKeys.FULL_NAME] = fullName
    }
}

suspend fun DataStore<Preferences>.savePlaces(places: List<RecommendedPlacesQuery.RecommendedPlace?>) {
    val serializable = places.map { it?.toSerializable() }
    val jsonString = Json.encodeToString(serializable)
    edit { preferences ->
        preferences[PreferencesKeys.PLACES] = jsonString
    }
}


// FETCH FUNCTIONS
fun DataStore<Preferences>.getApiTokenFlow(): Flow<String?> {
    return data.map { preferences ->
        preferences[PreferencesKeys.API_TOKEN]
    }
}

fun DataStore<Preferences>.getFullNameFlow(): Flow<String?> {
    return data.map { preferences ->
        preferences[PreferencesKeys.FULL_NAME]
    }
}

fun DataStore<Preferences>.getPlacesFlow(): Flow<List<RecommendedPlacesQuery.RecommendedPlace>> {
    return data.map { preferences ->
        val jsonString = preferences[PreferencesKeys.PLACES]
        if (jsonString != null) {
            val list = Json.decodeFromString<List<SerializablePlace>>(jsonString)
            list.map { it.toApolloModel() }
        } else {
            emptyList()
        }
    }
}


// CLEAR FUNCTIONS
suspend fun DataStore<Preferences>.clearAllData() {
    edit { preferences ->
        preferences.remove(PreferencesKeys.API_TOKEN)
    }
    edit { preferences ->
        preferences.remove(PreferencesKeys.FULL_NAME)
    }
    edit { preferences ->
        preferences.remove(PreferencesKeys.PLACES)
    }
}