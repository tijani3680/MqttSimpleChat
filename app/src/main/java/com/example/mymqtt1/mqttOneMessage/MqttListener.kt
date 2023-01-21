package com.example.mymqtt1.mqttOneMessage

import android.util.Log
import com.example.DataStoreManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.greenrobot.eventbus.EventBus

class MqttListener : IMqttMessageListener {
    val TAG = "mqtt_information_log"


    override fun messageArrived(topic: String?, message: MqttMessage?) {

        try {
            if ( message?.isDuplicate==true) return


            val messageData = createGson().fromJson(message.toString(), MessageModel::class.java)

                val chatItem =
                    if (messageData.id == MainActivity.CURRENTACCOUNT) ChatItem.Send(messageData.message)
                    else ChatItem.Receive(messageData.message)

            // val chatItemList = mutableListOf(chatItem)

            EventBus.getDefault().post(chatItem)

            Log.d(TAG, "messageData=$messageData")
            Log.d(TAG, "chatItem = $chatItem")
        } catch (ex: Exception) {
            Log.d(TAG, "$ex")
        }
    }

    private fun createGson(): Gson {
        return GsonBuilder().setLenient().create()
    }
}