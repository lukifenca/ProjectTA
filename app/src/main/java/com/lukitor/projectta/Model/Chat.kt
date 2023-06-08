package com.lukitor.projectta.Model

data class Chat(
    var id:String,
    var pesan:String,
    var jamKirim : String?,
    var tipePengirim: String?,
) {
    constructor():this("","","","")}
