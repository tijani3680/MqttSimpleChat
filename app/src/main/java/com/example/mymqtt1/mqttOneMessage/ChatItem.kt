package com.example.mymqtt1.mqttOneMessage

sealed class ChatItem {
    data class Receive(var message: String?) : ChatItem()

    data class Send(var message: String?) : ChatItem()
}