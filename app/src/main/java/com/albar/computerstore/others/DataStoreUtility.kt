package com.albar.computerstore.others

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreUtility @Inject constructor(appContext: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "onBoardingStatus")
    private val dataStoreContext = appContext.dataStore

    suspend fun save(key: String, value: Boolean) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStoreContext.edit { onBoarding ->
            onBoarding[dataStoreKey] = value.toString()
        }
    }

    fun getDataStore(key: String): Flow<String> {
        val dataStoreKey = stringPreferencesKey(key)
        return dataStoreContext.data.map { preferences ->
            preferences[dataStoreKey] ?: ""
        }
    }
}