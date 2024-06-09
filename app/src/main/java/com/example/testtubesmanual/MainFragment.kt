package com.example.testtubesmanual

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.testtubesmanual.adapter.WisataAdapter
import com.example.testtubesmanual.data.listWisata
import com.example.testtubesmanual.databinding.FragmentHomeBinding
import com.example.testtubesmanual.databinding.FragmentMainBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding?= null
    private val binding get() = _binding
    private lateinit var db: FirebaseDatabase
    private lateinit var destinationList : ArrayList<listWisata>
    private lateinit var adapter: WisataAdapter
    var selectedItemIndex = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.database
        destinationList = arrayListOf()
        adapter = WisataAdapter(destinationList)
        binding?.rvWisata?.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(),2)
        }
        fetchData()
        adapter.setOnItemClickCallback(object : WisataAdapter.OnItemClickCallback{
            override fun onItemClicked(data: listWisata) {
                val bundle = Bundle().apply {
                    putString("name", data.namalokasi)
                    putString("desc",data.deskripsi)
                    putString("photoUrl",data.photo)
                    putString("harga",data.harga)
                    putString("kategori", data.kategori)
                    putDouble("lat",data.lat)
                    putDouble("lng",data.lng)
                }
                moveToDetail(bundle)
            }

        })
        val searchView = binding?.searchBar
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchList(newText.toString())
                return true
            }

        })
        binding?.filterBtn?.setOnClickListener { filterDialog() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding?.root
    }
    private fun fetchData(){
        val ref = db.getReference("wisata")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                destinationList.clear()
                if (snapshot.exists()){
                    for (destinationSnap in snapshot.children){
                        val destination = destinationSnap.getValue(listWisata::class.java)
                        if (destination != null) {
                            destinationList.add(destination)
                        }
                    }
                }
                binding?.rvWisata?.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun filterAlam(){
        val ref = db.getReference("wisata")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                destinationList.clear()
                if (snapshot.exists()){
                    for (destinationSnap in snapshot.children){
                        val destination = destinationSnap.getValue(listWisata::class.java)
                        if (destination != null && destination.kategori == "Alam") {
                            destinationList.add(destination)
                        }
                    }
                }
                binding?.rvWisata?.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun filterWisata(){
        val ref = db.getReference("wisata")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                destinationList.clear()
                if (snapshot.exists()){
                    for (destinationSnap in snapshot.children){
                        val destination = destinationSnap.getValue(listWisata::class.java)
                        if (destination != null && destination.kategori == "Wisata Kuliner") {
                            destinationList.add(destination)
                        }
                    }
                }
                binding?.rvWisata?.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun filterShop(){
        val ref = db.getReference("wisata")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                destinationList.clear()
                if (snapshot.exists()){
                    for (destinationSnap in snapshot.children){
                        val destination = destinationSnap.getValue(listWisata::class.java)
                        if (destination != null && destination.kategori == "Pusat Perbelanjaan") {
                            destinationList.add(destination)
                        }
                    }
                }
                binding?.rvWisata?.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun moveToDetail(bundle: Bundle){
        Intent(requireActivity(),DetailActivity::class.java).also {
            it.putExtra(DetailActivity.DATA,bundle)
            startActivity(it)
        }
    }

    fun searchList(text:String){
        val searchList = ArrayList<listWisata>()
        for (destinationClass in destinationList){
            if (destinationClass.namalokasi.lowercase().contains(text)){
                searchList.add(destinationClass)
            }
        }
        adapter.searchDataList(searchList)
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
                if (selectedCategory == "Alam"){
                    filterAlam()
                }
                else if (selectedCategory == "Wisata Kuliner"){
                    filterWisata()
                }
                else if (selectedCategory == "Pusat Perbelanjaan"){
                    filterShop()
                }
                else{
                    fetchData()
                }
            }
            .show()

    }
}