package com.lukitor.projectta.Model

data class Pasien(
    var id:String,
    var email:String,
    var nama: String?,
    var tinggibadan : Int?,
    var beratbadan : Int?,
    var jeniskelamin : String?,
    var tanggallahir: String?,
    var role: String?,
    var jumlahvoucher: Int?
) {
    constructor():this("","","",0,0,"","","",0)
}
