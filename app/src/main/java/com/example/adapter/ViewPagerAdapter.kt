package com.example.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(val items:ArrayList<Fragment>,activity: FragmentManager):FragmentPagerAdapter(activity){


    override fun getCount(): Int {
         return items.size
    }

    override fun getItem(position: Int): Fragment {
        return items[position]
    }

}