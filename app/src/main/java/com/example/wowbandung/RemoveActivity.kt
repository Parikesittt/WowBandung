package com.example.wowbandung

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wowbandung.adapter.WisataAdapter
import com.example.wowbandung.adapter.WisataEditAdapter
import com.example.wowbandung.data.listWisata
import com.example.wowbandung.databinding.ActivityRemoveBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class RemoveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRemoveBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var progressDialog: ProgressDialog
    private lateinit var adapter: WisataEditAdapter
    private lateinit var destinationList:ArrayList<listWisata>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRemoveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        db = Firebase.database
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        destinationList = arrayListOf()
        adapter = WisataEditAdapter(destinationList)
        showLoading(true)
        fetchData()
        binding.rvRemove.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context,2)
        }
        adapter.setOnItemClickCallback(object : WisataEditAdapter.OnItemClickCallback{
            override fun onItemClicked(data: listWisata) {
                AlertDialog.Builder(this@RemoveActivity).apply {
                    setTitle("Hapus")
                    setMessage("Ingin menghapus wisata ini?")
                    setPositiveButton("Ya"){_, _ ->
                        removeWisata(data.namalokasi)
                        finish()
                    }
                    setNegativeButton("Tidak",null)
                    create()
                    show()
                }.apply {
                    setOnCancelListener{
                        finish()
                    }
                    show()
                }
            }

        })

    }
    private fun fetchData(){
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
                binding.rvRemove.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RemoveActivity,"Error : $error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun removeWisata(namalokasi:String){
        progressDialog.setMessage("Menghapus wisata...")
        progressDialog.show()
        val ref = db.getReference("wisata")
        ref.child("$namalokasi data")
            .removeValue()
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Wisata telah terhapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Gagal menghapus wisata : ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLoading(isLoading:Boolean){
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}