package com.example.mymqtt1.mqttOneMessage

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.mymqtt1.databinding.LayoutItemReceiveMessageBinding

class ChatLeftViewHolder(parent: ViewGroup) : BaseViewHolder<LayoutItemReceiveMessageBinding>(
    LayoutItemReceiveMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

) {
    fun bind(item: ChatItem.Receive) = with(itemView) {
        binding.txtReceiveMessage.text = item.message

    }
}
