package com.example.chatapp.ui

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
       supportActionBar?.hide()
        Handler(Looper.getMainLooper()).postDelayed({
            if (user != null) {
                val dashboardIntent = Intent(this, ChatActivity::class.java)
                val options = ActivityOptions.makeCustomAnimation(
                    this,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                this.startActivity(dashboardIntent, options.toBundle())
                finish()
            } else {
                val signInIntent = Intent(this, LoginActivity::class.java)
                val options = ActivityOptions.makeCustomAnimation(
                    this,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                this.startActivity(signInIntent, options.toBundle())
                finish()

            }

        },1000)


    }
}