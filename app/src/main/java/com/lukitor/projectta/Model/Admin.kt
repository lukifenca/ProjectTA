package com.lukitor.projectta.Model

data class Admin(
    var id:String,
    var email:String,
    var nama: String?,
    var telp: String?,
    var jeniskelamin : String?,
    var role: String?,
) {
    constructor():this("","","","","","")
}