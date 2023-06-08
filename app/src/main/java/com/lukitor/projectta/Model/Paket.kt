package com.lukitor.projectta.Model

data class Paket(
    var id:String,
    var namaPaket:String,
    var harga : Int?,
    var durasi: Int?,
    var jmldeteksi : Int?,
    var jmlvocer : Int?,
    var nominalvocer : Int?
) {
    constructor():this("","",0,0,0,0,0)
}
