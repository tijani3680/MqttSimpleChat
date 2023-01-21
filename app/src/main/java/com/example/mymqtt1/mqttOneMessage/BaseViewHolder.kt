package com.example.mymqtt1.mqttOneMessage

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class BaseViewHolder<T : ViewBinding>(
    protected val binding: T
) : RecyclerView.ViewHolder(binding.root)
