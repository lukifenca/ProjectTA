package com.lukitor.projectta.activityPasien.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.lukitor.projectta.Model.HistoryPaket
import com.lukitor.projectta.R

class HistoryPaketAdapter(val mCtx: Context, val layoutResId: Int, val mhsList:List<HistoryPaket>):
    ArrayAdapter<HistoryPaket>(mCtx,layoutResId,mhsList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val tvNama: TextView = view.findViewById(R.id.tv_profilenamapasien)
        val tvDeteksi: TextView = view.findViewById(R.id.txtDeteksi)
        val tvVocer: TextView = view.findViewById(R.id.txtVocer)
        val tvTglBeli: TextView = view.findViewById(R.id.txtDiskon)
        val tvTglExp: TextView = view.findViewById(R.id.txtHarga)
        val tvDurasi: TextView = view.findViewById(R.id.txtDurasi)
        var tvEdit: TextView = view.findViewById(R.id.txtEdit)
        tvDurasi.visibility = View.GONE
        tvEdit.visibility = View.GONE

        val mahasiswa = mhsList[position]

        tvNama.text = "Paket " + mahasiswa.namaPaket
        tvDeteksi.text = "Sisa Deteksi : " + mahasiswa.jmldeteksi
        tvVocer.text = "Sisa Voucher : " + mahasiswa.jmlvocer
        tvTglBeli.text = "Tanggal Beli :  " + mahasiswa.tanggalbeli
        tvTglExp.text = "Tanggal Exp :" + mahasiswa.tanggalexp


        return view
    }
}