package com.lukitor.projectta.Model

data class HistorySaldo(
    var id:String,
    var saldoawal: Int,
    var akunsaldo: Int,
    var saldoakhir: Int,
    var tipe: Int,
    var status : Int,
) {
    constructor():this("",0,0,0,0,0)}
