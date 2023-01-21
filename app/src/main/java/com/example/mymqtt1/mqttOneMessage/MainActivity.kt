package com.example.mymqtt1.mqttOneMessage

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymqtt1.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {
    private lateinit var mqttManager: MqttManager

    private lateinit var chatRecyclerAdapter: ChatRecyclerAdapter
    private val mCoroutinesScope = CoroutineScope(Dispatchers.Main)

    companion object {
        const val TAG = "AndroidMqttClient"
        var CURRENTACCOUNT = ""
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        mqttManager = MqttManager(this)
        if (!::chatRecyclerAdapter.isInitialized) chatRecyclerAdapter = ChatRecyclerAdapter()
        recyclerview.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = chatRecyclerAdapter
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
                CURRENTACCOUNT = if (p2 == 0)
                    MqttManager.MY_ACCOUNT_ID
                else
                    MqttManager.OTHER_ACCOUNT_ID
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(this@MainActivity, "Please Select Account", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //STOPSHIP
            }
        }


        btn_connect.setOnClickListener {
            mCoroutinesScope.launch {
                spinAccount.isEnabled=false
                mqttManager.connect(CURRENTACCOUNT)
                mqttManager.subscribe()
            }

        }
        btn_disconnect.setOnClickListener {
            mCoroutinesScope.launch {
                spinAccount.isEnabled=true
                mqttManager.disconnect()
            }

        }

        imgSendMessage.setOnClickListener {
            val message = edt_send_message.text.toString()
            if (message.isNotEmpty()) {
                edt_send_message.text?.clear()
                val data = "{\"id\": \"$CURRENTACCOUNT\",\"message\": \"$message\"}"
                mCoroutinesScope.launch {
                    mqttManager.sendMessage(data)
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
    fun serviceSendSignal(chatItems: ChatItem) {
        chatRecyclerAdapter.submitChats(chatItems).also {
            recyclerview.postDelayed({
                recyclerview.smoothScrollToPosition(chatRecyclerAdapter.itemCount - 1)
            }, 100)
        }
    }
}