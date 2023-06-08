package com.lukitor.projectta.activityDokter.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.lukitor.projectta.Model.HistoryTransaksi
import com.lukitor.projectta.Model.Pasien
import com.lukitor.projectta.R
import java.util.*

class ListAntrianAdapter(val mCtx: Context, val layoutResId: Int, val historyAntrian:List<HistoryTransaksi>,val listPasien:List<Pasien>, val tipe:Int):
    ArrayAdapter<HistoryTransaksi>(mCtx,layoutResId,historyAntrian) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val nama : TextView = view.findViewById(R.id.txtNamaPasienr)
        val usia : TextView = view.findViewById(R.id.txtUsiaPasien)
        val tv : TextView = view.findViewById(R.id.textView6)
        val img : ImageView = view.findViewById(R.id.imageprofilekonsultasidokter)
        val historySaldo = historyAntrian[position]
        val pasien= listPasien[position]
        nama.text = pasien.nama
        if (tipe == 1){
            tv.text = "Mulai Konsultasi"
        }
        else{
            tv.text = "Buka Chat"
        }
        val dob: Calendar = Calendar.getInstance()
        val today: Calendar = Calendar.getInstance()
        val tgllhair = pasien.tanggallahir
        val gender = pasien.jeniskelamin
        if (gender =="Pria"){
            img.setImageResource(R.drawable.ic_man);
        }
        else{
            img.setImageResource(R.drawable.ic_woman);
        }
        val mString = tgllhair!!.split("-").toTypedArray()
        dob.set(mString[2].toInt(),mString[1].toInt(),mString[0].toInt())
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        val agetemp = "$age Tahun"
        usia.text = agetemp

        return view
    }
}