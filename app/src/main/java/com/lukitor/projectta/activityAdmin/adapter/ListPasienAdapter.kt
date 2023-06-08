package com.lukitor.projectta.activityAdmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.lukitor.projectta.Model.Pasien
import com.lukitor.projectta.R
import java.util.*
import kotlin.math.tan

class ListPasienAdapter (val mCtx: Context, val layoutResId: Int, val listAdmin:List<Pasien>):
    ArrayAdapter<Pasien>(mCtx,layoutResId,listAdmin)  {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)
        var tb : TextView = view.findViewById(R.id.txtTinggi)
        var umur : TextView = view.findViewById(R.id.txtUmur)
        var berat : TextView = view.findViewById(R.id.txtBerat)
        var nama : TextView = view.findViewById(R.id.tv_profilenamapasien)
        var img : ImageView = view.findViewById(R.id.imageprofiledokter)
        var admin = listAdmin[position]
        var tanggallahir = admin.tanggallahir

        val dob: Calendar = Calendar.getInstance()
        val today: Calendar = Calendar.getInstance()
        val mString = tanggallahir!!.split("-").toTypedArray()
        dob.set(mString[2].toInt(),mString[1].toInt(),mString[0].toInt())
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        val ageInt = age
        val ageS = "$ageInt Tahun"
        nama.text = admin.nama
        berat.text = admin.beratbadan.toString() + " Kg"
        tb.text = admin.tinggibadan.toString() + " Cm"
        umur.text = ageS
        if (admin.jeniskelamin == "Pria"){
            img.setImageResource(R.drawable.ic_man)
        }
        else {
            img.setImageResource(R.drawable.ic_woman)
        }
        return view
    }
}