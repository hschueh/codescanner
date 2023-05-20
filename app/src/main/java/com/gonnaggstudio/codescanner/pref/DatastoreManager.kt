package com.gonnaggstudio.codescanner.pref

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings")

@Singleton
class DatastoreManager @Inject constructor(@ApplicationContext appContext: Context) {

    private val settingsDataStore = appContext.dataStore

    suspend fun saveBoolean(key: String, value: Boolean) = withContext(Dispatchers.IO) {
        settingsDataStore.edit { settings ->
            settings[booleanPreferencesKey(key)] = value
        }
    }

    fun readBooleans(vararg key: String): Flow<Map<String, Boolean>> {
        val preferencesKeys = key.map(::booleanPreferencesKey)
        return settingsDataStore.data.map { preferences ->
            preferencesKeys.associate {
                it.name to (preferences[it] ?: false)
            }
        }.flowOn(Dispatchers.IO)
    }
}
