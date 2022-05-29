package com.example.chatapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.Constants.Constants
import com.example.adapter.ChatAdapter
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityChatMessagesBinding
import com.example.models.ChatMessages
import com.example.models.LatestChat
import com.example.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ChatMessagesActivity : AppCompatActivity() {
    companion object{
        val TAG="ChatMessagesActivity"
    }
    private lateinit var binding: ActivityChatMessagesBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var chatMessages: ArrayList<ChatMessages>
    private lateinit var user:User

    private lateinit var chatAdapter: ChatAdapter
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_messages)
        auth = Firebase.auth
        init()
        listenMessages()
        supportActionBar?.hide()
        binding.apply {
            sendButton.setOnClickListener {
                sendmessage()
                binding.message.text=null
            }
            back.setOnClickListener {
                onBackPressed()
            }
        }
        setContentView(binding.root)
    }

    private fun sendmessage() {
        val uid = FirebaseAuth.getInstance().uid ?: ""

        val message=ChatMessages(uid,user.uid, binding.message.text.toString(), Date())
        binding.apply {
            db.collection("Messages").add(message).addOnSuccessListener {
                 Log.d(TAG , "Messgae send")
            }
                .addOnFailureListener {
                    Log.d(TAG , "Messgae send")
                }
             val u=LatestChat(user.username,user.profileImageUrl,binding.message.text.toString())
            db.collection(uid).add(u)
        }
    }
    private fun listenMessages() {
        db.collection("Messages")
            .whereEqualTo(
               "senderId",
                FirebaseAuth.getInstance().uid ?: ""
            )
            .whereEqualTo("receiverId", user.uid)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen:error", e)
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    val count = chatMessages.size
                    for (dc in snapshots.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                val message = ChatMessages(
                                    senderId = dc.document.getString("senderId")!!,
                                    receiverId = dc.document.getString("receiverId")!!,
                                    message = dc.document.getString("message"),
                                    dateTime = dc.document.getDate("dateTime")!!,
                                )
                                message.message=message.message?.trim()
                                chatMessages.add(message)
                            }
                            else -> {}
                        }
                    }
                    chatMessages.sortBy { it.dateTime }
                    if (count == 0) {
                        chatAdapter.notifyDataSetChanged()
                    } else {
                        chatAdapter.notifyItemRangeInserted(count, chatMessages.size - count)
                        binding.chatrecyclerview.smoothScrollToPosition(chatMessages.size - 1)
                    }
                    binding.chatrecyclerview.visibility = View.VISIBLE
                }

            }

       db.collection("Messages")
            .whereEqualTo("senderId", user.uid)
            .whereEqualTo(
                "receiverId",
                FirebaseAuth.getInstance().uid ?: ""
            )
           .addSnapshotListener { snapshots, e ->
               if (e != null) {
                   Log.w(TAG, "Listen:error", e)
                   return@addSnapshotListener
               }
               if (snapshots != null) {
                   val count = chatMessages.size
                   for (dc in snapshots.documentChanges) {
                       when (dc.type) {
                           DocumentChange.Type.ADDED -> {
                               val message = ChatMessages(
                                   senderId = dc.document.getString("senderId")!!,
                                   receiverId = dc.document.getString("receiverId")!!,
                                   message = dc.document.getString("message"),
                                   dateTime = dc.document.getDate("dateTime")!!,
                               )
                               message.message=message.message?.trim()
                               chatMessages.add(message)
                           }
                           else -> {}
                       }
                   }
                   chatMessages.sortBy { it.dateTime }
                   if (count == 0) {
                       chatAdapter.notifyDataSetChanged()
                   } else {
                       chatAdapter.notifyItemRangeInserted(count, chatMessages.size - count)
                       binding.chatrecyclerview.smoothScrollToPosition(chatMessages.size - 1)
                   }
                   binding.chatrecyclerview.visibility = View.VISIBLE
               }

           }
    }
    private fun init() {
        user=intent.getSerializableExtra(Constants.KEY_USER) as User
        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.ic_launcher_round)
            .error(R.mipmap.ic_launcher_round)
        Glide.with(this).load(user.profileImageUrl.toUri()).apply(options).into(binding.imageProfile)
        binding.textName.text=user.username
        binding.textStatus.text=user.status
        var user1=User("","","","")
        chatMessages = ArrayList()
        val uid = FirebaseAuth.getInstance().uid ?: ""
        db.collection("user").document(uid).get().addOnSuccessListener { document ->
            if (document != null) {
                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                user1=User(document.get("uid").toString(),document.get("username").toString(),document.get("profileImageUrl").toString(),document.get("status").toString())


            } else {
                Log.d(TAG, "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        chatAdapter = ChatAdapter(
            chatMessages,
            uid,
            user,
            user1,
            this)
        binding.chatrecyclerview.adapter = chatAdapter




    }
}