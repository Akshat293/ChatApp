package com.example.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentAboutBinding


class AboutFragment : Fragment() {

    private lateinit var binding:FragmentAboutBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)
        binding.apply {

            Next.setOnClickListener{

                view?.findNavController()?.navigate(R.id.action_aboutFragment_to_logInFragment)
            }


        }
        return binding.root
    }


}