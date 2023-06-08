package com.lukitor.projectta.activityPasien.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.lukitor.projectta.Model.Paket
import com.lukitor.projectta.R
import java.text.NumberFormat

class ListPaketAdapter (val mCtx: Context, val layoutResId: Int, val mhsList:List<Paket>):
    ArrayAdapter<Paket>(mCtx,layoutResId,mhsList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val tvNama: TextView = view.findViewById(R.id.tv_profilenamapasien)
        val tvDeteksi: TextView = view.findViewById(R.id.txtDeteksi)
        val tvVocer: TextView = view.findViewById(R.id.txtVocer)
        val tvDiskon: TextView = view.findViewById(R.id.txtDiskon)
        val tvHarga: TextView = view.findViewById(R.id.txtHarga)
        val tvDurasi: TextView = view.findViewById(R.id.txtDurasi)
        var tvEdit: TextView = view.findViewById(R.id.txtEdit)

        val mahasiswa = mhsList[position]

        tvNama.text = "Paket " + mahasiswa.namaPaket
        tvDeteksi.text = "Deteksi : " + mahasiswa.jmldeteksi
        tvVocer.text = "Voucher : " + mahasiswa.jmlvocer
        var hrgdiskon = NumberFormat.getInstance().format(mahasiswa.nominalvocer)
        tvDiskon.text = "Diskon : Rp. " + hrgdiskon + ",-"
        hrgdiskon = NumberFormat.getInstance().format(mahasiswa.harga)
        tvHarga.text = "Harga : Rp. "  + hrgdiskon + ",-"
        tvDurasi.text = "Durasi : " + mahasiswa.durasi + " Hari"
        tvEdit.text = "Beli"
        tvEdit.setTextColor(Color.parseColor("#FF4CAF50"))


        return view
    }

}
