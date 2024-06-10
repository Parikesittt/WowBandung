package com.example.wowbandung

import android.app.ProgressDialog
import android.content.Intent
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
import com.example.wowbandung.databinding.ActivityEditProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEditProfileBinding
    private lateinit var auth:FirebaseAuth
    private var currentImageUri:Uri?=null
    private lateinit var storage: FirebaseStorage
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        storage = Firebase.storage
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        val currentUser = auth.currentUser
        val userData = hashMapOf(
            "photoUrl" to currentUser?.photoUrl,
            "email" to currentUser?.email,
            "phoneNumber" to currentUser?.phoneNumber,
            "name" to currentUser?.displayName
        )
        binding.apply {
            Glide.with(this@EditProfileActivity)
                .load(userData["photoUrl"])
                .centerCrop()
                .into(ivProfile)
            nameEditText.setText(userData["name"].toString())
            emailEditText.setText(userData["email"].toString())
        }
        binding.ivProfile.setOnClickListener { startGallery() }
        binding.updateBtn.setOnClickListener { setNewUserData() }
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
            binding.ivProfile.setImageURI(it)
        }
    }

    private fun setNewUserData(){
        progressDialog.setMessage("Fetching new user data...")
        progressDialog.show()
        val storeRef = storage.reference.child("Users").child(System.currentTimeMillis().toString())
        if (currentImageUri == null){
            val updatedName = binding.nameEditText.text.toString()
            updatingUserDataWithoutImage(updatedName)
            Toast.makeText(this, "Berhasil mengupdate profile", Toast.LENGTH_SHORT).show()
        }else{
            currentImageUri?.let { uri ->
                storeRef.putFile(uri)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            storeRef.downloadUrl.addOnSuccessListener { photo ->
                                val updatedPhoto = photo
                                val updatedName = binding.nameEditText.text.toString()
                                updatingUserData(updatedName,updatedPhoto)
                                Toast.makeText(this, "Berhasil mengupdate profile", Toast.LENGTH_SHORT).show()
                            }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Gagal mengupdate profile", Toast.LENGTH_SHORT).show()
                                }
                        }
                        else{
                            Toast.makeText(this, "Gagal mengupload gambar", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
    private fun updatingUserData(name:String, photo:Uri){
        progressDialog.setMessage("Updating user data...")
        progressDialog.show()
        val user = auth.currentUser
        val profile = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .setPhotoUri(photo)
            .build()
        user?.updateProfile(profile)
            ?.addOnCompleteListener {
                if (it.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MainActivity::class.java))
                }
            }
    }
    private fun updatingUserDataWithoutImage(name:String){
        progressDialog.setMessage("Updating user data...")
        progressDialog.show()
        val user = auth.currentUser
        val profile = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()
        user?.updateProfile(profile)
            ?.addOnCompleteListener {
                if (it.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MainActivity::class.java))
                }
            }
    }
}