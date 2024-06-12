package com.example.wowbandung.adapter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wowbandung.DetailActivity
import com.example.wowbandung.data.listWisata
import com.example.wowbandung.databinding.ItemWisataBinding
import androidx.core.util.Pair

class WisataEditAdapter(private var destinationList: ArrayList<listWisata>):RecyclerView.Adapter<WisataEditAdapter.MyViewHolder>(){

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }
    inner class MyViewHolder(val binding: ItemWisataBinding):RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    onItemClickCallback?.onItemClicked(destinationList[position])
                }
            }
        }
        fun bind(data:listWisata){
            binding.apply {
                Glide.with(itemView)
                    .load(data.photo)
                    .centerCrop()
                    .into(ivWisata)
                tvNama.text = data.namalokasi
                tvAlamat.text = data.alamat
                tvHarga.text = data.harga
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemWisataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return destinationList.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = destinationList[position]
        if (data != null){
            holder.bind(data)
        }
    }

    interface OnItemClickCallback{
        fun onItemClicked(data:listWisata)
    }
}