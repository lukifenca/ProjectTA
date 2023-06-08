package com.lukitor.projectta.Model

data class Berita(
    var id:String,
    var judul:String,
    var konten: String,
    var tanggal: String
) {
    constructor():this("","","","")
}
