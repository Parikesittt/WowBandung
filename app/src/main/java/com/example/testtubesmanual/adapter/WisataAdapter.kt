package com.example.testtubesmanual.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.bumptech.glide.Glide
import com.example.testtubesmanual.data.listWisata
import com.example.testtubesmanual.databinding.ItemWisataBinding

class WisataAdapter(private val destinationList: ArrayList<listWisata>):RecyclerView.Adapter<WisataAdapter.MyViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }
    inner class MyViewHolder(val binding:ItemWisataBinding):RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    onItemClickCallback?.onItemClicked(destinationList[position])
                }
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
        val currentItem = destinationList[position]
        holder.apply {
            binding.apply {
                Glide.with(itemView)
                    .load(currentItem.photo)
                    .centerCrop()
                    .into(ivWisata)
                tvNama.text = currentItem.namalokasi
                tvAlamat.text = currentItem.alamat
                tvHarga.text = currentItem.harga
            }
        }
    }

    interface OnItemClickCallback{
        fun onItemClicked(data:listWisata)
    }
}