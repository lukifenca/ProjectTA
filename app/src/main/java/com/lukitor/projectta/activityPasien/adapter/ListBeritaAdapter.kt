package com.lukitor.projectta.activityPasien.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import com.lukitor.projectta.Model.Berita
import com.lukitor.projectta.R
import com.squareup.picasso.Picasso

class ListBeritaAdapter (val mCtx: Context, val layoutResId: Int, val mhsList:List<Berita>):
    ArrayAdapter<Berita>(mCtx,layoutResId,mhsList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val tvNama: TextView = view.findViewById(R.id.txtJudul)
        val tvTanggal: TextView = view.findViewById(R.id.txtTanggal)
        val img: ImageView = view.findViewById(R.id.imageView32)

        val mahasiswa = mhsList[position]

        tvNama.text = mahasiswa.judul
        tvTanggal.text = mahasiswa.tanggal
        Picasso.get().load("https://i.imgur.com/XeVG9tV.jpg").into(img)

        return view
    }

}
