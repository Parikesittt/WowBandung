package com.example.testtubesmanual

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.testtubesmanual.adapter.WisataAdapter
import com.example.testtubesmanual.data.listWisata
import com.example.testtubesmanual.databinding.ActivityEditWisataBinding
import com.example.testtubesmanual.databinding.ActivityRemoveBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

class EditWisataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditWisataBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var progressDialog: ProgressDialog
    private lateinit var namaTempat:String
    private lateinit var desc : String
    private lateinit var photoUrl : String
    private lateinit var harga : String
    private lateinit var kategoriWisata:String
    private lateinit var alamatWisata:String
    private lateinit var lat:String
    private lateinit var lng:String
    private  var currentImageUri:Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditWisataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        db = Firebase.database
        storage = Firebase.storage
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_button)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val bundle = intent.getBundleExtra(DetailActivity.DATA)
        namaTempat = bundle?.getString("name").toString()
        desc = bundle?.getString("desc").toString()
        photoUrl = bundle?.getString("photoUrl").toString()
        harga = bundle?.getString("harga").toString()
        kategoriWisata = bundle?.getString("kategori").toString()
        alamatWisata = bundle?.getString("alamat").toString()
        lat = bundle?.getDouble("lat").toString()
        lng = bundle?.getDouble("lng").toString()

        if (bundle != null){
            binding.apply {
                Glide.with(this@EditWisataActivity)
                    .load(photoUrl)
                    .centerCrop()
                    .into(previewImage)
                judulText.setText(namaTempat)
                DeskripsiEditText.hint = desc
                budgetEditTextLayout.hint = harga
                kategoriEditTextLayout.hint = kategoriWisata
                alamatEditTextLayout.hint = alamatWisata
                latEditText.setText("$lat")
                lngEditText.setText("$lng")
            }
        }
        binding.previewImage.setOnClickListener { startGallery() }
        binding.edtButton.setOnClickListener { updateData() }
    }

    private fun updateData(){
        progressDialog.setMessage("Updating data wisata...")
        progressDialog.show()
        val storeRef = storage.reference.child("Images").child(System.currentTimeMillis().toString())
        currentImageUri?.let { uri ->
            storeRef.putFile(uri)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        storeRef.downloadUrl.addOnSuccessListener { photo ->
                            progressDialog.dismiss()
                            val updatedPhoto = photo.toString()
                            val updatedDesc = binding.DeskripsiText.text.toString()
                            val updatedAlamat = binding.alamatEditText.text.toString()
                            val updatedKategori = binding.kategoriEditText.text.toString()
                            val updatedHarga = binding.budgetEditText.text.toString()
                            setUpdatedData(namaTempat,updatedDesc,updatedHarga,updatedAlamat,updatedKategori,lat.toDouble(),lng.toDouble(),updatedPhoto)
                            Toast.makeText(this, "Berhasil mengupdate wisata", Toast.LENGTH_SHORT).show()
                        }
                            .addOnFailureListener {
                                progressDialog.dismiss()
                                Toast.makeText(this , "Gagal mengupdate wisata", Toast.LENGTH_SHORT).show()
                            }
                    }
                    else{
                        progressDialog.dismiss()
                        Toast.makeText(this, "Gagal mengupload gambar", Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }
    private fun setUpdatedData(
        namalokasi:String,
        deskripsi:String,
        harga:String,
        alamat:String,
        kategori:String,
        lat:Double,
        lng:Double,
        photo:String
    ){
        progressDialog.setMessage("Updating data wisata...")
        progressDialog.show()
        val ref = db.getReference("wisata").child("$namaTempat data")
        val updatedData = hashMapOf(
            "alamat" to alamat,
            "deskripsi" to deskripsi,
            "harga" to harga,
            "kategori" to kategori,
            "lat" to lat,
            "lng" to lng,
            "namalokasi" to namalokasi,
            "photo" to photo
        )
        ref.setValue(updatedData)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Wisata sudah di update", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(this,"Gagal update wisata : ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun startGallery(){
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){uri: Uri? ->
        if (uri != null){
            currentImageUri = uri
            showImage()
        }else{
            Log.d("Photo Picker", "No Media Selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage : $it")
            binding.previewImage.setImageURI(it)
        }
    }
    companion object{
        const val DATA = "data"
    }
}