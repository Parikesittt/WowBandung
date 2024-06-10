package com.example.wowbandung

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wowbandung.adapter.WisataAdapter
import com.example.wowbandung.data.listWisata
import com.example.wowbandung.databinding.FragmentFavoriteBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding?=null
    private val binding get() = _binding
    private lateinit var favoriteList : ArrayList<listWisata>
    private lateinit var rvAdapter: WisataAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    var selectedItemIndex = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteList = arrayListOf()
        auth = Firebase.auth
        db = Firebase.database
        rvAdapter = WisataAdapter(favoriteList)
        showLoading(true)
        fetchData()
        binding?.rvFav?.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireActivity(),2)
        }
        binding?.appbarItem?.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.filter -> {
                    filterDialog()
                    true
                }
                else -> false
            }
        }
        rvAdapter.setOnItemClickCallback(object : WisataAdapter.OnItemClickCallback{
            override fun onItemClicked(data: listWisata) {
                val bundle = Bundle().apply {
                    putString("name", data.namalokasi)
                    putString("desc",data.deskripsi)
                    putString("photoUrl",data.photo)
                    putString("harga",data.harga)
                    putString("kategori", data.kategori)
                    putString("alamat", data.alamat)
                    putDouble("lat",data.lat)
                    putDouble("lng",data.lng)
                }
                Intent(requireActivity(), DetailActivity::class.java).also {
                    it.putExtra(DetailActivity.DATA, bundle)
                    startActivity(it)
                }
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater,container,false)
        return binding?.root
    }
    private fun fetchData(){
        val dbUsers = db.getReference("Users")
        dbUsers.child(auth.currentUser?.uid.toString()).child("Favorites")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    showLoading(false)
                    favoriteList.clear()
                    if (snapshot.exists()){
                        for (favoriteSnap in snapshot.children){
                            val favorite = favoriteSnap.getValue(listWisata::class.java)
                            if (favorite != null){
                                favoriteList.add(favorite)
                            }
                        }
                    }
                    binding?.rvFav?.adapter = rvAdapter
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context,"Error : $error", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun filterDialog(){
        val category = arrayOf("All", "Alam", "Wisata Kuliner", "Pusat Perbelanjaan")
        var selectedCategory = category[selectedItemIndex]

        AlertDialog.Builder(requireContext())
            .setTitle("Pilih kategori")
            .setSingleChoiceItems(category, selectedItemIndex) {dialog, which ->
                selectedItemIndex = which
                selectedCategory = category[which]
            }
            .setPositiveButton("OK") {dialog, which ->
                when (selectedCategory) {
                    "Alam" -> {
                        filterAlam()
                    }
                    "Wisata Kuliner" -> {
                        filterWisata()
                    }
                    "Pusat Perbelanjaan" -> {
                        filterShop()
                    }
                    else -> {
                        fetchData()
                    }
                }
            }
            .show()
    }
    private fun filterAlam(){
        val ref = db.getReference("Users").child(auth.currentUser?.uid.toString()).child("Favorite")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                showLoading(false)
                favoriteList.clear()
                if (snapshot.exists()){
                    for (favoriteSnap in snapshot.children){
                        val favorite = favoriteSnap.getValue(listWisata::class.java)
                        if (favorite != null && favorite.kategori == "Alam") {
                            favoriteList.add(favorite)
                        }
                    }
                }
                binding?.rvFav?.adapter = rvAdapter
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun filterWisata(){
        val ref = db.getReference("Users").child(auth.currentUser?.uid.toString()).child("Favorite")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                showLoading(false)
                favoriteList.clear()
                if (snapshot.exists()){
                    for (favoriteSnap in snapshot.children){
                        val favorite = favoriteSnap.getValue(listWisata::class.java)
                        if (favorite != null && favorite.kategori == "Wisata Kuliner") {
                            favoriteList.add(favorite)
                        }
                    }
                }
                binding?.rvFav?.adapter = rvAdapter
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun filterShop(){
        val ref = db.getReference("Users").child(auth.currentUser?.uid.toString()).child("Favorite")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                showLoading(false)
                favoriteList.clear()
                if (snapshot.exists()){
                    for (favoriteSnap in snapshot.children){
                        val favorite = favoriteSnap.getValue(listWisata::class.java)
                        if (favorite != null && favorite.kategori == "Pusat Perbelanjaan") {
                            favoriteList.add(favorite)
                        }
                    }
                }
                binding?.rvFav?.adapter = rvAdapter
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showLoading(isLoading:Boolean){
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}