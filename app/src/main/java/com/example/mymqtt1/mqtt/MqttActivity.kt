package com.example.mymqtt1.mqtt

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.DataStoreManager
import com.example.mymqtt1.R
import com.example.mymqtt1.mqttOneMessage.ChatItem
import com.example.mymqtt1.mqttOneMessage.ChatRecyclerAdapter1
import com.example.mymqtt1.mqttOneMessage.MqttManager
import com.example.room.ChatEntity
import com.example.room.RoomManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MqttActivity : AppCompatActivity() {
    private lateinit var mqttManager2: MqttManager2
    private lateinit var chatRecyclerAdapter1: ChatRecyclerAdapter1
    private val mCoroutinesScope = CoroutineScope(Dispatchers.Main)

    companion object {
        const val TAG = "AndroidMqttClient"
        var CURRENTACCOUNT = ""
            private set
    }

    private var myChatEntities = mutableListOf<ChatEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mqtt)
        supportActionBar?.hide()
        EventBus.getDefault().register(this)
        mqttManager2 = MqttManager2(this)
        btn_disconnect.setBackgroundColor(Color.GRAY)
        btn_disconnect.isEnabled=false
        if (!::chatRecyclerAdapter1.isInitialized) chatRecyclerAdapter1 = ChatRecyclerAdapter1()
        recyclerview.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = chatRecyclerAdapter1
        }
        val accountList = resources.getStringArray(R.array.account_array)

        ArrayAdapter(this, android.R.layout.simple_spinner_item, accountList).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }.also {
            spinAccount.adapter = it
        }

        spinAccount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                mCoroutinesScope.launch {
                    DataStoreManager.getInstance(this@MqttActivity)?.saveAccount(p2)

                }
                CURRENTACCOUNT = if (p2 == 0)
                    MqttManager.MY_ACCOUNT_ID
                else
                    MqttManager.OTHER_ACCOUNT_ID
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(this@MqttActivity, "Please Select Account", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //STOPSHIP
            }
        }


        mCoroutinesScope.launch {
            withContext(Dispatchers.IO) {
                DataStoreManager.getInstance(this@MqttActivity)?.getAccount()?.first()?.let {
                    spinAccount.setSelection(it,true)
                    spinAccount.isEnabled=false

                    CURRENTACCOUNT = if (it == 0)
                        MqttManager.MY_ACCOUNT_ID
                    else
                        MqttManager.OTHER_ACCOUNT_ID

                }

                RoomManager.getInstance(this@MqttActivity)?.chatDao?.getChats()
                    ?.onEach { chatEntitys ->
                        Log.d("ofodidieigje", "$chatEntitys")
                        myChatEntities = chatEntitys.toMutableList()
                        val chatItems = mutableListOf<ChatItem>()


                        chatEntitys.forEach {
                            val chatItem =
                                if (it.id == CURRENTACCOUNT) ChatItem.Send(it.message)
                                else ChatItem.Receive(it.message)

                            chatItems.add(chatItem)
                        }
                        withContext(Dispatchers.Main){
                            chatRecyclerAdapter1.submitChats(chatItems).also {
                                recyclerview.postDelayed({
                                    if (chatRecyclerAdapter1.itemCount !=0)
                                        recyclerview.smoothScrollToPosition(chatRecyclerAdapter1.itemCount - 1)
                                }, 100)
                            }
                        }


                    }?.collect()
            }
        }


        btn_connect.setOnClickListener {
            mCoroutinesScope.launch {
                spinAccount.isEnabled = false
                mqttManager2.connect(CURRENTACCOUNT)
                mqttManager2.subscribe()
            }

        }
        btn_disconnect.setOnClickListener {
            mCoroutinesScope.launch {
                mqttManager2.disconnect()
            }

        }

        imgSendMessage.setOnClickListener {
            val message = edt_send_message.text.toString()
            if (message.isNotEmpty()) {
                mCoroutinesScope.launch {
                    edt_send_message.text?.clear()
                    val oneMessage = ChatEntity(userId = null,id = CURRENTACCOUNT, message = message)
                    myChatEntities.add(oneMessage)

                    val finalMessageList = createGson().toJson(myChatEntities)

                    //
                    // val oneMessage = "{\"id\": \"$CURRENTACCOUNT\",\"message\": \"$message\"}"
                    // myChatEntitys.add(oneMessage)
                    // Log.d("Tijanijoniidid", "$myChatEntitys")
                    mqttManager2.sendMessage(finalMessageList)
                }
            }

        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        mCoroutinesScope.cancel()
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun serviceSendSignal(status:MqttStatusModel) {
     when(status){

         MqttStatusModel.DeliveryComplete -> {

         }
         MqttStatusModel.DisConnect -> {
             btn_connect.setBackgroundColor(Color.parseColor("#1A4468"))
             btn_connect.isEnabled=true

             btn_disconnect.setBackgroundColor(Color.GRAY)
             btn_disconnect.isEnabled=false
             Toast.makeText(this, "Disconnected...", Toast.LENGTH_SHORT).show()
             status_text.text="disconnected"
             status_text.setTextColor(Color.RED)

         }
         is MqttStatusModel.Connect ->{
             btn_connect.setBackgroundColor(Color.GRAY)
             btn_connect.isEnabled=false

             btn_disconnect.setBackgroundColor(Color.parseColor("#D80A0A"))
             btn_disconnect.isEnabled=true

             if (status.reconnect){
                 status_text.text="reconnected"
                 status_text.setTextColor(Color.BLUE)

             }else{
                 status_text.text="connected"
                 status_text.setTextColor(Color.GREEN)

             }

         }
     }
    }

    private fun createGson(): Gson {
        return GsonBuilder().setLenient().create()
    }
}