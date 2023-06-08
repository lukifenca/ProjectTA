package com.lukitor.projectta.Model

data class Obat(
    var id:String,
    var idpenyakit:String,
    var namaobat: String,
) {
    constructor():this("","","")
}
