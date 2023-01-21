package com.example.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ChatEntity::class],
    version = 1,
    exportSchema = false
)
internal abstract class RoomManager : RoomDatabase() {
    abstract val chatDao: ChatDao

    companion object {
        private var instance: RoomManager? = null

        @Synchronized
        open fun getInstance(context: Context): RoomManager? {
            if (instance == null)
                instance = databaseBuilder(context, RoomManager::class.java, "MqttDB")
                    .fallbackToDestructiveMigration()
                    .build()
            return instance
        }
    }
}
