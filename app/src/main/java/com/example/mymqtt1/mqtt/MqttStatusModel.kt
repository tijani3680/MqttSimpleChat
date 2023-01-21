package com.example.mymqtt1.mqtt

sealed class MqttStatusModel{
    data class Connect( val reconnect:Boolean):MqttStatusModel()
    object DisConnect:MqttStatusModel()
    object DeliveryComplete:MqttStatusModel()
}
