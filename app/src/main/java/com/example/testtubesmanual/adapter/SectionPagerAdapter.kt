package com.example.testtubesmanual.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.testtubesmanual.AllWisataFragment
import com.example.testtubesmanual.FavoriteFragment
import com.example.testtubesmanual.HomeFragment
import com.example.testtubesmanual.ProfileUserFragment
import com.example.testtubesmanual.WisataAlamFragment
import com.example.testtubesmanual.WisataKulinerFragment
import com.example.testtubesmanual.WisataShopFragment

class SectionPagerAdapter(activity:Fragment) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        var fragment:Fragment? = null
        when(position) {
            0 -> fragment = AllWisataFragment()
            1 -> fragment = WisataAlamFragment()
            2 -> fragment = WisataKulinerFragment()
            3 -> fragment = WisataShopFragment()
        }
        return fragment as Fragment
    }


}