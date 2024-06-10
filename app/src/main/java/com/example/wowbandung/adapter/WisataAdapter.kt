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

class WisataAdapter(private var destinationList: ArrayList<listWisata>):RecyclerView.Adapter<WisataAdapter.MyViewHolder>(), Filterable {

    private var onItemClickCallback: OnItemClickCallback? = null

    var destinationListFiltered : ArrayList<listWisata> = ArrayList()

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
                itemView.setOnClickListener{
                    val bundle = Bundle().apply {
                        putString("name", data.namalokasi)
                        putString("desc",data.deskripsi)
                        putString("photoUrl",data.photo)
                        putString("harga",data.harga)
                        putString("kategori", data.kategori)
                        putString("alamat", data.alamat)
                        putDouble("lat",data.lat)
                        putDouble("lng",data.lng)
                    }
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.DATA, bundle)
                    val optionsCompat :ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(ivWisata, "destination"),
                            Pair(tvNama, "namalokasi")
                        )
                    itemView.context.startActivity(intent,optionsCompat.toBundle())
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

    fun searchDataList(searchList:ArrayList<listWisata>){
        destinationList = searchList
        notifyDataSetChanged()
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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) destinationListFiltered = destinationList else {
                    val filteredList = ArrayList<listWisata>()
                    destinationList
                        .filter {
                            (it.namalokasi.contains(constraint!!)) or
                                    (it.alamat.contains(constraint))

                        }
                        .forEach { filteredList.add(it) }
                    destinationListFiltered = filteredList

                }
                return FilterResults().apply { values = destinationListFiltered }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                destinationListFiltered = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<listWisata>
                notifyDataSetChanged()
            }
        }
    }
}