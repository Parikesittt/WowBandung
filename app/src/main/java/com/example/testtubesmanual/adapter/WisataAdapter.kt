package com.example.testtubesmanual.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.bumptech.glide.Glide
import com.example.testtubesmanual.data.listWisata
import com.example.testtubesmanual.databinding.ItemWisataBinding

class WisataAdapter(private var destinationList: ArrayList<listWisata>):RecyclerView.Adapter<WisataAdapter.MyViewHolder>(), Filterable {

    private var onItemClickCallback: OnItemClickCallback? = null

    var destinationListFiltered : ArrayList<listWisata> = ArrayList()

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

    fun searchDataList(searchList:ArrayList<listWisata>){
        destinationList = searchList
        notifyDataSetChanged()
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