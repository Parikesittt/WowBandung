package com.example.testtubesmanual.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.testtubesmanual.FavoriteFragment
import com.example.testtubesmanual.HomeFragment
import com.example.testtubesmanual.ProfileUserFragment

class SectionPagerAdapter(activity:AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        val fragment:Fragment? = null
        when(position) {
            0 -> HomeFragment()
            1 -> FavoriteFragment()
            2 -> ProfileUserFragment()
        }
        return fragment as Fragment
    }


}