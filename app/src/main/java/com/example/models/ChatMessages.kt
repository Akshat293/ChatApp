package com.example.models

import java.util.*

data class ChatMessages(val senderId:String,
                        val receiverId:String,
                        var message:String?,
                        val dateTime: Date,)
