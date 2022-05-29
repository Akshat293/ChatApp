package com.example.fragments

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentRegisterBinding
import com.example.models.User
import com.example.chatapp.ui.ChatActivity
import com.example.chatapp.ui.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class RegisterFragment : Fragment() {
    private lateinit var binding:FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    companion object {
        val TAG = "RegisterFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        auth = Firebase.auth
        binding.apply {

           login.setOnClickListener {

               activity?.onBackPressed();
            }
            back.setOnClickListener {
                activity?.onBackPressed();

            }
            upimg.setOnClickListener {

                if (ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) { // izin alınmadıysa
                    ActivityCompat.requestPermissions(requireActivity(),arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1)
                } else {
                    val galeriIntext = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galeriIntext,2)
                }
            }
            logbt.setOnClickListener {
                performRegister()
            }


        }
        return binding.root
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was...
            selectedPhotoUri = data.data
            if (Build.VERSION.SDK_INT >= 28) {
                Log.d(TAG, "Photo was selected")
                val source = activity?.let { ImageDecoder.createSource(it.contentResolver,selectedPhotoUri!!) }
                    val bitmap= source?.let { ImageDecoder.decodeBitmap(it) }
                binding.upimg.setImageBitmap(bitmap)
            }else{
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedPhotoUri)
                binding.upimg.setImageBitmap(bitmap)
            }

        }
    }

    private fun performRegister() {
        binding.progressBar.visibility=View.VISIBLE
        val email = binding.emailbt.text.toString()
        val password = binding.passbt.text.toString()
        val confpass=binding.cnfbt.text.toString()

        if (!(activity as LoginActivity).isValidEmail(email) || password.isEmpty() || confpass.isEmpty() || confpass!=password || confpass.length<6 || password.length<6) {
             if(!(activity as LoginActivity).isValidEmail(email)){
                 binding.apply {
                     emailbt.startAnimation((activity as LoginActivity).shakeError())
                     emailbt.error="Please Enter Valid Email Address."
                 }
             }else if( password.length<0){
                 binding.apply {
                     passbt.startAnimation((activity as LoginActivity).shakeError())
                     passbt.error = "Please enter a password of length greater than 6 "
                 }
             }else if(password!=confpass) {
                 binding.apply {
                     cnfbt.startAnimation((activity as LoginActivity).shakeError())
                     cnfbt.error = "Confirm password dosent match with the entered password "
                 }
             }

            return
        }

        Log.d(TAG, "Attempting to create user with email: $email")

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                // else if successful
                Log.d(TAG, "Successfully created user with uid: ${it.result.user?.uid}")

                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener{
                Log.d(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(requireContext(), "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) {
            Log.d(TAG, "Empty url")
            return
        }
        lifecycleScope.launch(Dispatchers.IO) {




            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d(TAG, "File Location: $it")

                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to upload image to storage: ${it.message}")
                }
        }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val uid = FirebaseAuth.getInstance().uid ?: ""
            val ref = db.collection("users").document(uid)

            val user = User(uid, binding.namebt.text.toString(), profileImageUrl,"Online")

            ref.set(user)
                .addOnSuccessListener {
                    Log.d(TAG, "Finally we saved the user to Firebase Database")
                    binding.progressBar.visibility=View.INVISIBLE
                    activity?.let{
                        val intent = Intent (it, ChatActivity::class.java)
                        val options =
                            ActivityOptions.makeCustomAnimation(requireContext(), R.anim.slide_in_right, R.anim.slide_out_left)
                        it.startActivity(intent,options.toBundle())
                        it.finish()
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to set value to database: ${it.message}")
                    binding.progressBar.visibility=View.INVISIBLE
                }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galeriIntext = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntext,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}





