package com.example.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentForgetPasswordBinding
import com.example.chatapp.ui.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgetPasswordFragment : Fragment() {

    private lateinit var binding:FragmentForgetPasswordBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forget_password, container, false)
        auth = Firebase.auth
        binding.apply {

            create.setOnClickListener{

                view?.findNavController()?.navigate(R.id.action_forgetPasswordFragment2_to_registerFragment)
            }
            back.setOnClickListener {
                activity?.onBackPressed();

            }
            logbt.setOnClickListener {
                if(isValidEmail(emailbt.text.toString())){
                    Sendmail(emailbt.text.toString())
                }else{
                    emailbt.startAnimation((activity as LoginActivity).shakeError())
                    emailbt.error = "Please Enter Valid Email Address."
                }
            }


        }
        return binding.root
    }
    fun Sendmail(email:String){
        binding.progressBar.visibility=View.VISIBLE
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.progressBar.visibility=View.INVISIBLE
                    Toast.makeText(requireContext(),"Email has been sent to your mail",Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Email sent.")
                }else{
                    binding.progressBar.visibility=View.INVISIBLE
                    Toast.makeText(requireContext(),"Failed to send email",Toast.LENGTH_SHORT).show()
                }
            }
    }


    fun isValidEmail(target: String?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}