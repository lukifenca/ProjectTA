package com.lukitor.projectta.activityPasien.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.lukitor.projectta.Model.HistoryPaket
import com.lukitor.projectta.Model.HistoryTransaksi
import com.lukitor.projectta.R
import java.text.NumberFormat

class HistoryKonsultasiPasienAdapter(val mCtx: Context, val layoutResId: Int, val mhsList:List<HistoryTransaksi>):
    ArrayAdapter<HistoryTransaksi>(mCtx,layoutResId,mhsList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val tvNama: TextView = view.findViewById(R.id.txtTanggalKonsultasi)
        val tvDeteksi: TextView = view.findViewById(R.id.txtTotalKonsultasi)

        val mahasiswa = mhsList[position]

        tvNama.text = mahasiswa.tanggal
        val temp = mahasiswa.tarifKonsultasi + mahasiswa.feeKonsultasi
        val temp2 = NumberFormat.getInstance().format(temp)
        tvDeteksi.text = "Rp. $temp2,-"


        return view
    }
}