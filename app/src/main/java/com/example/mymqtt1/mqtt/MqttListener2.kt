package com.example.mymqtt1.mqtt

import android.content.Context
import android.util.Log
import com.example.room.ChatEntity
import com.example.room.RoomManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttMessage

class MqttListener2(private val context: Context) : IMqttMessageListener {
    val TAG = "mqtt_information_log"
    private val mCoroutinesScope = CoroutineScope(Dispatchers.IO)

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        mCoroutinesScope.launch {

            try {
                if (message?.isDuplicate == true) return@launch

                val messageModels =  createGson().fromJson<ArrayList<ChatEntity>>(
                    message.toString(),
                    object : TypeToken<ArrayList<ChatEntity?>?>() {}.type
                )

                RoomManager.getInstance(context)?.chatDao?.clearChats()

                RoomManager.getInstance(context)?.chatDao?.insertChats(messageModels)


            } catch (ex: Exception) {
                Log.d(TAG, "$ex")
            }

        }

    }
    private fun createGson(): Gson {
        return GsonBuilder().setLenient().create()
    }
}