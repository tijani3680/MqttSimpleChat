package com.example.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "chats")
data class ChatEntity(

    @PrimaryKey(autoGenerate = true)
    val userId: Int?,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("message")
    val message: String
)
