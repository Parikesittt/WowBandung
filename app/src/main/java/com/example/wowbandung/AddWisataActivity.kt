package com.example.wowbandung

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
import com.example.wowbandung.databinding.ActivityAddWisataBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

class AddWisataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddWisataBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var progressDialog: ProgressDialog
    private var currentImageUri: Uri?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddWisataBinding.inflate(layoutInflater)
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
        binding.previewImage.setOnClickListener { startGallery() }
        binding.addButton.setOnClickListener { uploadWisata() }
    }

    private fun startGallery(){
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun uploadWisata(){
        progressDialog.setMessage("Menambahkan wisata...")
        progressDialog.show()
        val storeRef = storage.reference.child("Images").child(System.currentTimeMillis().toString())
        currentImageUri?.let { uri ->
            storeRef.putFile(uri)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        storeRef.downloadUrl.addOnSuccessListener {photo ->
                            val imageUri = photo.toString()
                            Log.d("ImageFile", "uploadImage: $imageUri")
                            val nama = binding.judulText.text.toString()
                            val deskripsi = binding.DeskripsiText.text.toString()
                            val alamat = binding.alamatEditText.text.toString()
                            val kategori = binding.kategoriEditText.text.toString()
                            val harga = binding.budgetEditText.text.toString()
                            val lat = binding.latEditText.text.toString().toDouble()
                            val lng = binding.lngEditText.text.toString().toDouble()
                            val ref = db.getReference("wisata")
                            val dataWisata = hashMapOf(
                                "alamat" to alamat,
                                "deskripsi" to deskripsi,
                                "harga" to harga,
                                "kategori" to kategori,
                                "namalokasi" to nama,
                                "photo" to imageUri,
                                "lat" to lat,
                                "lng" to lng
                            )
                            ref.child("$nama data")
                                .setValue(dataWisata)
                                .addOnSuccessListener {
                                    progressDialog.dismiss()
                                    Toast.makeText(this,"Wisata berhasil di upload", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener{ e->
                                    progressDialog.dismiss()
                                    Toast.makeText(this,"Gagal upload wisata : ${e.message}",Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Gagal mengupload gambar", Toast.LENGTH_SHORT).show()
                    }
                }
        }
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
}