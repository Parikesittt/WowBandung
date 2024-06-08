package com.example.testtubesmanual.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testtubesmanual.data.listWisata
import com.example.testtubesmanual.databinding.ItemWisataInRemoveBinding

class DeleteWisataAdapter: ListAdapter<listWisata, DeleteWisataAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }
    inner class MyViewHolder(val binding: ItemWisataInRemoveBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener{
                val position =adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    onItemClickCallback?.onItemClicked(getItem(position))
                }
            }
        }
        fun bind(wisata: listWisata){
            binding.apply {
                Glide.with(itemView)
                    .load(wisata.photo)
                    .into(ivWisata)
                tvNama.text = wisata.namalokasi
                tvAlamat.text = wisata.alamat
                tvHarga.text = wisata.harga
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemWisataInRemoveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: listWisata)
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<listWisata>(){
            override fun areItemsTheSame(oldItem: listWisata, newItem: listWisata): Boolean {
                return oldItem.namalokasi == newItem.namalokasi
            }

            override fun areContentsTheSame(oldItem: listWisata, newItem: listWisata): Boolean {
                return oldItem == newItem
            }

        }
    }
}