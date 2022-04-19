package com.albar.computerstore.others

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.albar.computerstore.others.Constants.PREFERENCES_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreUtility @Inject constructor(appContext: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)
    private val dataStoreContext = appContext.dataStore

    suspend fun save(key: String, value: Boolean) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStoreContext.edit { onBoarding ->
            onBoarding[dataStoreKey] = value.toString()
        }
    }

    fun getDataStore(key: String): Flow<Boolean> {
        val dataStoreKey = stringPreferencesKey(key)
        return dataStoreContext.data.map { preferences ->
            preferences[dataStoreKey].toBoolean()
        }
    }
}