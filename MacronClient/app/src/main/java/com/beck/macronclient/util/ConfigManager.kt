package com.beck.macronclient.util

import android.content.Context
import android.content.ContextWrapper
import androidx.datastore.preferences.core.stringPreferencesKey
import com.beck.macronclient.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ConfigManager(val context: Context) {
    val AUTH_KEY = stringPreferencesKey("user_config")
    fun getApiKey(): Flow<String?> {
        return context.dataStore.data.map {
            it[AUTH_KEY]
        }
    }
}