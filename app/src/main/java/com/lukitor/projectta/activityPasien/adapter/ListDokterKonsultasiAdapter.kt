package com.lukitor.projectta.activityPasien.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.lukitor.projectta.Model.Dokter
import com.lukitor.projectta.R
import com.squareup.picasso.Picasso
import java.text.NumberFormat

class ListDokterKonsultasiAdapter (val mCtx: Context, val layoutResId: Int, val listDokter:List<Dokter>):
    ArrayAdapter<Dokter>(mCtx,layoutResId,listDokter) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        var img : ImageView = view.findViewById(R.id.imageprofilekonsultasidokter)
        var nama : TextView = view.findViewById(R.id.txtNamaDokter)
        var lamapraktek : TextView = view.findViewById(R.id.txtKonsulLamaPraktek)
        var tarif : TextView = view.findViewById(R.id.txtHargaKonsultasi)
        var dokter = listDokter[position]
        nama.text = dokter.nama
        val praktek = dokter.lamapraktek.toString() +" Tahun"
        lamapraktek.text = praktek
        val myString = NumberFormat.getInstance().format(dokter.tarif);
        tarif.text = "Rp. " + myString + ",-"
        val uid = dokter.id
        val ref = Firebase.storage.reference.child("images/$uid/fotoprofile")
        ref.downloadUrl
            .addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(img)
                img.visibility = View.VISIBLE
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Image loading failed", exception)
            }

        img.setImageResource(R.drawable.wallenegatif);
        return view
    }
}