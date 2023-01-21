package com.example.mymqtt1.mqttOneMessage

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ChatRecyclerAdapter1 :
    ListAdapter<ChatItem, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<ChatItem>() {
        override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            val oldContent =
                (oldItem as? ChatItem.Receive)?.message ?: (oldItem as? ChatItem.Send)?.message
            val newContent =
                (newItem as? ChatItem.Receive)?.message ?: (newItem as? ChatItem.Send)?.message
            return oldContent == newContent
        }
    }) {

    companion object {
        private const val SEND_MESSAGE = 0
        private const val RECEIVE_MESSAGE = 1
    }

    private var mList: List<ChatItem>? = null

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is ChatItem.Send -> SEND_MESSAGE
            is ChatItem.Receive -> RECEIVE_MESSAGE
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
                (currentList[position] as? ChatItem.Receive)?.let { holder.bind(it) }
            }
            is ChatRightViewHolder -> {
                (currentList[position] as? ChatItem.Send)?.let { holder.bind(it) }
            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }



    fun submitChats(chats: List<ChatItem>) {
        val mChats = chats.toMutableList()
        submitList(mChats)
    }

    override fun submitList(list: MutableList<ChatItem>?) {
        mList = list
        super.submitList(list)
    }

}
