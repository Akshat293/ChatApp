package com.listners

import com.example.models.User

interface UserListner {
    fun onUserClicked(user: User)
}