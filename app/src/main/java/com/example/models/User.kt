package com.example.models


import java.io.Serializable

data class User(val uid: String, val username: String, val profileImageUrl: String, val status:String): Serializable
