package com.example.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatapp.R
import com.example.chatapp.databinding.ItemContainerUserBinding
import com.example.models.User
import com.listners.UserListner

 class UserAdapter(val users: List<User>, val userListener: UserListner, val context: Context): RecyclerView.Adapter<UserAdapter.UserViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val itemContainerUserBinding=com.example.chatapp.databinding.ItemContainerUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(itemContainerUserBinding)
    }


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.setUserData(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class UserViewHolder(val binding: ItemContainerUserBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun setUserData(user: User){
            binding.textName.text = user.username
            binding.textStatus.text=user.status
            val options: RequestOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
            Glide.with(context).load(user.profileImageUrl.toUri()).apply(options).into(binding.imageProfile)
            binding.root.setOnClickListener{
                userListener.onUserClicked(user)
            }
        }
    }


}