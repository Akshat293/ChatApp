package com.example.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.chatapp.R
import com.example.chatapp.databinding.ChatFromRowBinding
import com.example.chatapp.databinding.ChatToRowBinding
import com.example.models.ChatMessages
import com.example.models.User

class ChatAdapter(
    private val chatMessages: java.util.ArrayList<ChatMessages>,
    private val senderId: String,
    private val user: User,
    private val user1: User,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val MESSAGE_TYPE_SENT_TEXT = 1
        const val MESSAGE_TYPE_RECEIVE_TEXT = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MESSAGE_TYPE_SENT_TEXT -> SentMessageViewHolder(
                ChatToRowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            MESSAGE_TYPE_RECEIVE_TEXT -> ReceiveMessageViewHolder(
               ChatFromRowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> {
                  ReceiveMessageViewHolder(
                ChatFromRowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if( getItemViewType(position) == MESSAGE_TYPE_SENT_TEXT){
            (holder as SentMessageViewHolder).setData(chatMessage = chatMessages[position])
        }else{
            (holder as ReceiveMessageViewHolder).setData(chatMessage = chatMessages[position])
        }

    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        if (chatMessages[position].senderId == senderId) {
            return MESSAGE_TYPE_SENT_TEXT
            }
         else {
            return MESSAGE_TYPE_RECEIVE_TEXT
            }

    }

    inner class SentMessageViewHolder(val binding: ChatToRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(chatMessage: ChatMessages) {
            binding.textView.text = chatMessage.message
            val options: RequestOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
            Log.d("ChatAdapter",user1.profileImageUrl)
            Glide.with(context).load(user.profileImageUrl.toUri()).apply(options).into(binding.imageView)

        }
    }

    inner class ReceiveMessageViewHolder(val binding: ChatFromRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(chatMessage: ChatMessages) {
            binding.textView.text = chatMessage.message
            val options: RequestOptions = RequestOptions()
                .centerCrop()
                .centerCrop()
                .placeholder(R.drawable.ic_account)
            Log.d("ChatAdapter",user.profileImageUrl)
            Glide.with(context).load(user.profileImageUrl.toUri()).apply(options).into(binding.imageView)
        }
    }





}