package com.albar.computerstore.others

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class DataStoreUtility(val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "onBoardingStatus")

    private suspend fun save(key: String, value: Boolean) {
        val dataStoreKey = stringPreferencesKey(key.toString())
        context.dataStore.edit { onBoarding ->
            onBoarding[dataStoreKey] = value.toString()
        }
    }

    private suspend fun run(key: String): Boolean {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences =
            context.dataStore.data.first() // emit single preferences object using first()
        return preferences[dataStoreKey].toBoolean()
    }
}