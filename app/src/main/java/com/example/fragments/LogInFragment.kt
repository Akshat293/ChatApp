package com.example.fragments

import android.app.ActivityOptions
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentLogInBinding
import com.example.chatapp.ui.ChatActivity
import com.example.chatapp.ui.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LogInFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding:FragmentLogInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_log_in, container, false)
        auth = Firebase.auth
        binding.apply {

            create.setOnClickListener {

                view?.findNavController()?.navigate(R.id.action_logInFragment_to_registerFragment)
            }
            back.setOnClickListener {
                activity?.onBackPressed();

            }
            ftbt.setOnClickListener {
                view?.findNavController()?.navigate(R.id.action_logInFragment_to_forgetPasswordFragment2)
            }
            logbt.setOnClickListener {
              if(isValidEmail(emailbt.text.toString()) && passbt.text.toString().length>=6){
                  Login(emailbt.text.toString(),passbt.text.toString())
              }else if(!isValidEmail(emailbt.text.toString())){
                  emailbt.startAnimation((activity as LoginActivity).shakeError())
                  emailbt.error = "Please Enter Valid Email Address."
              }else if(passbt.text.toString().length<6){
                  passbt.startAnimation((activity as LoginActivity).shakeError())
                  passbt.error="Please enter a password of length greater than 6 "
              }
            }
        }
        return binding.root
    }
    private fun isValidEmail(target: String?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
fun  Login( email:String , password:String){
    binding.progressBar.visibility=View.VISIBLE
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                binding.progressBar.visibility=View.INVISIBLE
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                activity?.let{
                    val intent = Intent (it, ChatActivity::class.java)
                    val options =
                        ActivityOptions.makeCustomAnimation(requireContext(), R.anim.slide_in_right, R.anim.slide_out_left)
                    it.startActivity(intent,options.toBundle())
                    it.finish()
                }
            } else {
                binding.progressBar.visibility=View.INVISIBLE
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(requireContext(), "Authentication failed.",
                    Toast.LENGTH_SHORT).show()

            }
        }
}

}