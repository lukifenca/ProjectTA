package com.lukitor.projectta.Model

data class HistoryPaket(
    var id:String,
    var namaPaket:String,
    var harga : Int?,
    var tanggalbeli: String?,
    var tanggalexp: String?,
    var jmldeteksi : Int?,
    var jmlvocer : Int?,
    var nominalvocer : Int?,
    var idPasien : String
) {
    constructor():this("","",0,"","",0,0,0,"")
}
