package com.example.wowbandung

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wowbandung.adapter.WisataAdapter
import com.example.wowbandung.data.listWisata
import com.example.wowbandung.databinding.ActivityEditBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: WisataAdapter
    private lateinit var destinationList:ArrayList<listWisata>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        db = Firebase.database
        destinationList = arrayListOf()
        adapter = WisataAdapter(destinationList)
        showLoading(true)
        fetchData()
        binding.rvEdit.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context,2)
        }
        adapter.setOnItemClickCallback(object : WisataAdapter.OnItemClickCallback{
            override fun onItemClicked(data: listWisata) {
                val bundle = Bundle().apply {
                    putString("name", data.namalokasi)
                    putString("desc",data.deskripsi)
                    putString("photoUrl",data.photo)
                    putString("harga",data.harga)
                    putString("alamat",data.alamat)
                    putString("kategori",data.kategori)
                    putDouble("lat",data.lat)
                    putDouble("lng",data.lng)
                }
                moveToEdit(bundle)
            }

        })
    }
    private fun fetchData() {
        val ref = db.getReference("wisata")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                showLoading(false)
                destinationList.clear()
                if (snapshot.exists()){
                    for (destinationSnap in snapshot.children){
                        val destination = destinationSnap.getValue(listWisata::class.java)
                        if (destination != null) {
                            destinationList.add(destination)
                        }
                    }
                }
                binding.rvEdit.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditActivity,"Error : $error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun moveToEdit(bundle:Bundle){
        Intent(this,EditWisataActivity::class.java).also {
            it.putExtra(EditWisataActivity.DATA,bundle)
            startActivity(it)
        }
    }

    private fun showLoading(isLoading:Boolean){
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}