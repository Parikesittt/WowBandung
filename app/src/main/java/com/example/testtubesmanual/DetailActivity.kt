package com.example.testtubesmanual

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.testtubesmanual.RegisterActivity.Companion.TAG
import com.example.testtubesmanual.data.listWisata
import com.example.testtubesmanual.databinding.ActivityDetailBinding
import com.example.testtubesmanual.databinding.ActivityLoginBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class DetailActivity : AppCompatActivity(), OnMapReadyCallback{
    private lateinit var binding:ActivityDetailBinding
    private lateinit var mMap: GoogleMap
    private lateinit var namaTempat:String
    private lateinit var desc : String
    private lateinit var photoUrl : String
    private lateinit var harga : String
    private lateinit var kategori:String
    private var lat:Double?=null
    private var lng:Double?=null
    private lateinit var db:FirebaseDatabase
    private lateinit var auth:FirebaseAuth
    private var isFav:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        db = Firebase.database
        val mapFragment = supportFragmentManager.findFragmentById(R.id.maps) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        val bundle = intent.getBundleExtra(DATA)
        namaTempat = bundle?.getString("name").toString()
        desc = bundle?.getString("desc").toString()
        photoUrl = bundle?.getString("photoUrl").toString()
        harga = bundle?.getString("harga").toString()
        kategori = bundle?.getString("kategori").toString()
        lat = bundle?.getDouble("lat")
        lng = bundle?.getDouble("lng")
        if (bundle != null){
            binding.apply {
                Glide.with(this@DetailActivity)
                    .load(photoUrl)
                    .centerCrop()
                    .into(ivTempat)
                tvName.text = namaTempat
                tvDesc.text = desc
                tvHarga.text = harga
            }
        }

        checkFav()
        binding.toggleFav.setOnClickListener {
            if (isFav){
                removeFav()
            }
            else{
                addFavorite()
            }
        }

    }

    private fun addFavorite(){
        Log.d(TAG, "addFavorite: Adding to fav")
        val currentUser = auth.currentUser

        val favPlace = hashMapOf(
            "namalokas" to namaTempat,
            "photo" to photoUrl,
            "deskripsi" to desc,
            "budget" to harga,
            "kategori" to kategori
        )

        val ref = db.getReference("Users")
        ref.child(currentUser?.uid.toString()).child("Favorites").child(namaTempat)
            .setValue(favPlace)
            .addOnSuccessListener {
                Log.d(TAG, "addFavorite: Added to fav")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "addFavorite: Failed to add to fav due to ${e.message}")
                Toast.makeText(this, "Failed to add to fav due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeFav(){
        Log.d(TAG, "removeFav: Removing from fav")
        val currentUser = auth.currentUser
        val ref = db.getReference("Users")
        ref.child(currentUser?.uid.toString()).child("Favorites").child(namaTempat)
            .removeValue()
            .addOnSuccessListener { Log.d(TAG, "removeFav: Removed from fav") }
            .addOnFailureListener { e->
                Log.d(TAG, "removeFav: Failed to remove from fav due to ${e.message}")
                Toast.makeText(this, "Failed to remove from fav due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkFav(){
        Log.d(TAG, "checkFav: Checking if destination is in fav or not")
        val btnFav = binding.toggleFav
        val currentUser = auth.currentUser
        val ref = db.getReference("Users")
        ref.child(currentUser?.uid.toString()).child("Favorites").child(namaTempat)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    isFav = snapshot.exists()
                    btnFav.isChecked = isFav
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    companion object{
        const val DATA = "data"
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val dataMap = hashMapOf(
            "lat" to lat,
            "lng" to lng
        )
        mMap =googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        val dataLat = dataMap.get("lat").toString()
        val dataLng = dataMap.get("lng").toString()
        val lokasi = LatLng(dataLat.toDouble(),dataLng.toDouble())
        mMap.addMarker(
            MarkerOptions()
                .position(lokasi)
                .title(namaTempat)
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi,15f))
    }
}