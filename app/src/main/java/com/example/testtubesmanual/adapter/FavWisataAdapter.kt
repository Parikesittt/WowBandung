package com.example.testtubesmanual.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testtubesmanual.data.listWisata
import com.example.testtubesmanual.databinding.ActivityFavoriteBinding
import com.example.testtubesmanual.databinding.ItemWisataBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavWisataAdapter:RecyclerView.Adapter<FavWisataAdapter.MyViewHolder> {

    private val context : Context
    private val wisataArrayList : ArrayList<listWisata>
    private lateinit var binding: ItemWisataBinding
    constructor(context: Context, wisataArrayList: ArrayList<listWisata>){
        this.context = context
        this.wisataArrayList = wisataArrayList
    }

    inner class MyViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        var namaLokasi = binding.tvNama
        var alamatLokasi = binding.tvAlamat
        var hargaLokasi = binding.tvHarga
        var photoLokasi = binding.ivWisata
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = ItemWisataBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return wisataArrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = wisataArrayList[position]

        loadsFav(model,holder)
    }

    private fun loadsFav(model: listWisata, holder: FavWisataAdapter.MyViewHolder) {
        val namaTmpt = model.namalokasi

        val ref = FirebaseDatabase.getInstance().getReference("wisata")
        ref.child(namaTmpt)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val photo = "${snapshot.child("photo").value}"
                    val alamat = "${snapshot.child("alamat").value}"
                    val harga = "${snapshot.child("harga").value}"
                    holder.apply{
                        binding.apply {
                            Glide.with(context)
                                .load(photo)
                                .centerCrop()
                                .into(ivWisata)
                            tvNama.text = namaTmpt
                            tvAlamat.text = alamat
                            tvHarga.text = harga
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

}