package com.wuknow.pictionis

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.message_item.view.*


class MessageAdapter(val messages: ArrayList<Message>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindForecast(messages[position])
    }

    override fun getItemCount() = messages.size

    /***
     * Ajout du message
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindForecast(message: Message) {
            itemView.messageAdapterMessageItem.text =  message.name +  " : " + message.text
        }
    }
}