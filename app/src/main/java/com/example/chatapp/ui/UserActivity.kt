package com.example.chatapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Constants.Constants
import com.example.adapter.UserAdapter
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityUserBinding
import com.example.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.listners.UserListner

class UserActivity : AppCompatActivity(),UserListner {
    private lateinit var binding:ActivityUserBinding
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user)
        auth = Firebase.auth
        setContentView(binding.root)
        supportActionBar?.title="Select User"
        binding.apply {
            recyclerview.adapter
            recyclerview.layoutManager= LinearLayoutManager(this@UserActivity)


        }
        getUsers()
    }
    override fun onUserClicked(user: User) {
        val intent= Intent(applicationContext,ChatMessagesActivity::class.java)
        intent.putExtra(Constants.KEY_USER,user)
        startActivity(intent)
        finish()
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


                        val user=User(
                            uid=document.getString("uid")?:"",
                            username =document.getString("username")?:"name",
                            profileImageUrl =document.getString("profileImageUrl")?:"",
                            status =document.getString("status")?:"Offline"
                        )
                        users.add(user)
                    }
                    if(users.size>0){
                        val usersAdapter= UserAdapter(users,this,applicationContext)
                        binding.recyclerview.adapter=usersAdapter
                        binding.recyclerview.visibility=View.VISIBLE
                    }
                    else
                    {
                       Toast.makeText(applicationContext,"No user",Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(applicationContext,"No user",Toast.LENGTH_SHORT).show()
                }
            }
    }

}