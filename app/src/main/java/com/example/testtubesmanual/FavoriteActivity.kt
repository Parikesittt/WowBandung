package com.example.testtubesmanual

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.testtubesmanual.adapter.FavWisataAdapter
import com.example.testtubesmanual.adapter.WisataAdapter
import com.example.testtubesmanual.data.listWisata
import com.example.testtubesmanual.databinding.ActivityFavoriteBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFavoriteBinding
    private lateinit var adapter: FavWisataAdapter
    private lateinit var destinationList : ArrayList<listWisata>
    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        destinationList = arrayListOf()
        auth = Firebase.auth
        db = Firebase.database
        fetchData()
    }

    private fun fetchData(){
        val dbUsers = db.getReference("Users")
        dbUsers.child(auth.currentUser?.uid.toString()).child("Favorites")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    destinationList.clear()
                    if (snapshot.exists()){
                        for (destinationSnap in snapshot.children){
                            val namaTmpt = "${destinationSnap.child("nama").value}"

                          val modelDest = listWisata()
                            modelDest.namalokasi = namaTmpt

                         destinationList.add(modelDest)
                        }
                        adapter = FavWisataAdapter(this@FavoriteActivity,destinationList)
                        binding.rvFav.adapter = adapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext,"Error : $error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}