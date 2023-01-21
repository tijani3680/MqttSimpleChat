package com.example.mymqtt1.mqttOneMessage

import com.google.gson.annotations.SerializedName

data class MessageModel(

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("message")
	val message: String
)
