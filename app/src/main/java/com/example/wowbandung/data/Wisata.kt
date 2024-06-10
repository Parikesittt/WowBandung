package com.example.wowbandung.data

data class listWisata(
    val alamat:String = "",
    val deskripsi:String = "",
    val harga:String = "",
    val kategori:String="",
    val lat:Double = 0.0,
    val lng:Double = 0.0,
    var namalokasi:String = "",
    val photo:String = ""
)