package com.example.mymqtt1.mqtt

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.greenrobot.eventbus.EventBus

class MqttManager2(private val context: Context) {

    companion object {
        const val TAG = "mqtt_information_log"
        const val TOPIC = "Tijani"
        const val CONTENT = "Message from MqttPublishSample"
        const val QOS = 2
        const val BROKER = "ws://broker.emqx.io:8083"
        const val CLIENTID = "mqttx_39677462"

        const val MY_ACCOUNT_ID = "mqttx_39677462"
        const val OTHER_ACCOUNT_ID = "mqttx_f9913662"
    }

    private var mqttClient: MqttClient? = null
    private lateinit var mqttListener2: MqttListener2

    suspend fun connect(accountId: String) = withContext(Dispatchers.IO) {
        try {
            if (mqttClient?.isConnected == true) return@withContext
            mqttListener2 = MqttListener2(context)
            /*mqttClient = MqttClient(BROKER, accountId, MemoryPersistence()).apply {
                setCallback(object :MqttCallbackExtended{
                    override fun connectionLost(cause: Throwable?) {
                        Log.d("mytijanijonididieid","Disconnected......")
                    }

                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                        Log.d("mytijanijonididieid","messageArrived......")
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        Log.d("mytijanijonididieid","deliveryComplete......")
                    }

                    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                        Log.d("mytijanijonididieid","Connected......   $reconnect")

                    }
                })
            }*/
            mqttClient = MqttClient(BROKER, accountId, MemoryPersistence()).apply {
                setCallback(object : MqttCallbackExtended {
                    override fun connectionLost(cause: Throwable?) {
                        EventBus.getDefault().post(MqttStatusModel.DisConnect)
                    }

                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        EventBus.getDefault().post(MqttStatusModel.DeliveryComplete)
                    }

                    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                        EventBus.getDefault().post(MqttStatusModel.Connect(reconnect))
                    }
                })
            }
            val connOpts = MqttConnectOptions().apply {
                isCleanSession = true
                isAutomaticReconnect = true
            }


            generateLog("Connecting to broker: $BROKER")


            mqttClient?.connect(connOpts)


            generateLog("Connected")
        } catch (me: MqttException) {
            generateLog("reason " + me.reasonCode)
            generateLog("msg " + me.message)
            generateLog("loc " + me.localizedMessage)
            generateLog("cause " + me.cause)
            generateLog("excep $me")
            me.printStackTrace()
        }
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            mqttClient?.disconnect()
            EventBus.getDefault().post(MqttStatusModel.DisConnect)
            generateLog("Disconnected")
        } catch (ex: MqttException) {
            generateLog("excep $ex")
        }
    }

    suspend fun subscribe() = withContext(Dispatchers.IO) {
        try {
            mqttClient?.subscribe(TOPIC, QOS, mqttListener2)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    suspend fun sendMessage(message: String) = withContext(Dispatchers.IO) {
        if (mqttClient?.isConnected == true) {
            generateLog("Publishing message: $message")
            val mqttMessage = MqttMessage(message.toByteArray()).apply {
                qos = QOS; isRetained = true
            }
            mqttClient?.publish(TOPIC, mqttMessage)
            generateLog("Message published")
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "mqtt not connect!!!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun generateLog(message: String, data: String? = null) {
        Log.d(TAG, "message =  $message  //// data =  $data")
    }
}