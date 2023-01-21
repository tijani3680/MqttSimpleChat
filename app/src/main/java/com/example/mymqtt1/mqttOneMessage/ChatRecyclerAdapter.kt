package com.example.mymqtt1.mqttOneMessage

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ChatRecyclerAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val SEND_MESSAGE = 0
        private const val RECEIVE_MESSAGE = 1
    }

    private var mList: MutableList<ChatItem>? = mutableListOf()

    override fun getItemViewType(position: Int): Int {
        return when (mList?.get(position)) {
            is ChatItem.Send -> SEND_MESSAGE
            is ChatItem.Receive -> RECEIVE_MESSAGE
            else -> 2

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SEND_MESSAGE -> ChatRightViewHolder(parent)
            RECEIVE_MESSAGE -> ChatLeftViewHolder(parent)
            else -> ChatLeftViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChatLeftViewHolder -> {
                (mList?.get(position) as? ChatItem.Receive)?.let { holder.bind(it) }
            }
            is ChatRightViewHolder -> {
                (mList?.get(position) as? ChatItem.Send)?.let { holder.bind(it) }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitChats(chats: ChatItem) {
        mList?.add(chats)
        notifyDataSetChanged()
    }
}
