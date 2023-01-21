package com.example

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.room.RoomManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DataStoreManager(private val context: Context) {

    private val Context.dataStore by preferencesDataStore(DATA_STORE_KEY)

    companion object ConstDataStore {
        @SuppressLint("StaticFieldLeak")
        private var instance: DataStoreManager? = null

        private const val DATA_STORE_KEY = "data_store_key"

        private val ACCOUNT_KEY = intPreferencesKey("account_key")

        @Synchronized
        fun getInstance(context: Context): DataStoreManager? {
            if (instance == null)
                instance = DataStoreManager(context)
            return instance
        }

    }

    suspend fun clearAccount(): Unit = with(context) {
        dataStore.edit { it.remove(ACCOUNT_KEY) }
    }

    suspend fun clearDataStore(): Unit = with(context) {
        dataStore.edit { it.clear() }
    }

    suspend fun saveAccount(account: Int): Unit = with(context) {
        dataStore.edit { it[ACCOUNT_KEY] = account }
    }

    fun getAccount(): Flow<Int?> = context.dataStore.data.map {
        it[ACCOUNT_KEY]
    }
}
