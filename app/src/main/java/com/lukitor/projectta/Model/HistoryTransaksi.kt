package com.lukitor.projectta.Model

data class HistoryTransaksi(
    var id:String,
    var pasienId:String,
    var dokterId:String,
    var tarifKonsultasi: Int,
    var feeKonsultasi: Int,
    var statusVocer: Int,
    var tanggal: String,
    var jammulai: String,
    var jamselesai: String,
    var statusTrans: Int,
    var statusRM: Int,
    var statusFoto: Int,
    var deteksiPenyakit: String,
    var rating: Int

) {
    constructor():this("","","",0,0,0,"","","",0,0,0, "",5)}