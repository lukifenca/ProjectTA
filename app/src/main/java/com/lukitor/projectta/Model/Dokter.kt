package com.lukitor.projectta.Model

data class Dokter(
    var id:String,
    var email:String,
    var nama: String?,
    var telp: String?,
    var jeniskelamin : String?,
    var namabank: String?,
    var norek: String?,
    var atasnama: String?,
    var tarif: Int?,
    var tempatpraktek: String?,
    var lamapraktek: Int?,
    var role: String?,
    var statusakun: Int?,
    var saldo: Int?
) {
    constructor():this("","","","","","","","",0, "",0,  "", 0,0)}
