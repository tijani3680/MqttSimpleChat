package com.example.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<ChatEntity>)

    @Query("SELECT * FROM chats ORDER BY userId")
    fun getChats(): Flow<List<ChatEntity>>

    @Query("DELETE FROM chats")
    fun clearChats()
}
