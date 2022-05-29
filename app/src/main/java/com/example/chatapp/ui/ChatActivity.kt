package com.example.chatapp.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.adapter.ViewPagerAdapter
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.fragments.CallFragment
import com.example.fragments.ChatFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ChatActivity : AppCompatActivity() {
    private lateinit var binding:ActivityChatBinding
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        auth = Firebase.auth
        setContentView(binding.root)
        binding.apply {
            val fragment:ArrayList<Fragment> = arrayListOf(
                ChatFragment(),
                CallFragment()
            )
         viewpager.adapter=ViewPagerAdapter(fragment, supportFragmentManager)
         chatbt.backgroundTintList=(ContextCompat.getColorStateList(applicationContext,
             R.color.white
         ))
            chatbt.setTextColor(ContextCompat.getColorStateList(applicationContext,
                R.color.appcolor
            ))
            Updatebt()
          chatbt.setOnClickListener{
              if (getItem() !=0) {

                  viewpager.setCurrentItem(getItem() -1, true)
              }

          }
            callbt.setOnClickListener {


                    viewpager.setCurrentItem(getItem() + 1, true)

            }
            fab.setOnClickListener {

                val intent = Intent(this@ChatActivity, UserActivity::class.java)
                val options =
                    ActivityOptions.makeCustomAnimation(this@ChatActivity, R.anim.slide_in_left, R.anim.slide_out_right)
                startActivity(intent, options.toBundle())

            }
            viewpager.addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {}
                override fun onPageScrollStateChanged(state: Int) {
                    Updatebt()
                }
            })
        }

        supportActionBar?.hide()
    }
    private fun getItem(): Int {
        return binding.viewpager.currentItem
    }
    fun Updatebt(){
        binding.apply {
            if(viewpager.currentItem==0){
                chatbt.backgroundTintList=(ContextCompat.getColorStateList(applicationContext,
                    R.color.white
                ))
                chatbt.setTextColor(ContextCompat.getColorStateList(applicationContext,
                    R.color.appcolor
                ))
                callbt.backgroundTintList=(ContextCompat.getColorStateList(applicationContext,
                    R.color.appcolor
                ))
                callbt.setTextColor(ContextCompat.getColorStateList(applicationContext,
                    R.color.white
                ))
            }else{
                callbt.backgroundTintList=(ContextCompat.getColorStateList(applicationContext,
                    R.color.white
                ))
                callbt.setTextColor(ContextCompat.getColorStateList(applicationContext,
                    R.color.appcolor
                ))
                chatbt.backgroundTintList=(ContextCompat.getColorStateList(applicationContext,
                    R.color.appcolor
                ))
                chatbt.setTextColor(ContextCompat.getColorStateList(applicationContext,
                    R.color.white
                ))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val docRef = db.collection("users").document(uid)
        val updates = hashMapOf<String, Any>(
            "status" to "Offline"
        )

        docRef.update(updates).addOnCompleteListener {

        }
    }

    override fun onStart() {
        super.onStart()
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val docRef = db.collection("users").document(uid)
        val updates = hashMapOf<String, Any>(
            "status" to "Online"
        )

        docRef.update(updates).addOnCompleteListener { }
    }
}
