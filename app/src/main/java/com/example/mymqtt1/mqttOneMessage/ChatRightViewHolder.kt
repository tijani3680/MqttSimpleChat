package com.example.mymqtt1.mqttOneMessage

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.mymqtt1.databinding.LayoutItemSendMessageBinding

class ChatRightViewHolder(parent: ViewGroup) : BaseViewHolder<LayoutItemSendMessageBinding>(
    LayoutItemSendMessageBinding.inflate(LayoutInflater.from(parent.context),parent,false)

) {
    fun bind(item: ChatItem.Send) = with(itemView) {
        binding.txtSendMessage.text = item.message
    }
}
