package com.example.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Constants.Constants
import com.example.adapter.UserAdapter
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentChatBinding
import com.example.chatapp.ui.ChatMessagesActivity
import com.example.models.ChatMessages
import com.example.models.LatestChat
import com.example.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.listners.UserListner
import java.util.ArrayList


class ChatFragment : Fragment(),UserListner {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding:FragmentChatBinding
    val db = Firebase.firestore
    private lateinit var chatMessages: ArrayList<LatestChat>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        auth = Firebase.auth
        binding.apply {
            recyclerview.adapter
            recyclerview.layoutManager= LinearLayoutManager(requireContext())
        }
        getUsers()
        return binding.root

    }
    override fun onUserClicked(user: User) {
        val intent= Intent(requireContext(), ChatMessagesActivity::class.java)
        intent.putExtra(Constants.KEY_USER,user)
        startActivity(intent)
    }
    private fun loading(isLoading:Boolean)
    {

        when(isLoading){
            true-> binding.progressBar.visibility= View.VISIBLE
            else-> binding.progressBar.visibility= View.INVISIBLE
        }
    }
    private fun getUsers(){
        loading(true)


        db.collection("users")
            .get()
            .addOnCompleteListener{
                loading(false)
                val uid = FirebaseAuth.getInstance().uid ?: ""
                if(it.isSuccessful&&it.result!=null){
                    val users=ArrayList<User>()
                    for(document in it.result!!)
                    {
                        if(uid == document.getString("uid"))
                            continue


                        val user= User(
                            uid=document.getString("uid")?:"",
                            username =document.getString("username")?:"name",
                            profileImageUrl =document.getString("profileImageUrl")?:"",
                            status =document.getString("status")?:"Offline"
                        )
                        users.add(user)
                    }
                    if(users.size>0){
                        val usersAdapter= UserAdapter(users,this,requireContext())
                        binding.recyclerview.adapter=usersAdapter
                        binding.recyclerview.visibility=View.VISIBLE
                    }
                    else
                    {
                        Toast.makeText(requireContext(),"No user", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(requireContext(),"No user", Toast.LENGTH_SHORT).show()
                }
            }
    }

}