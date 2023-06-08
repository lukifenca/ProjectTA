package com.lukitor.projectta.activityAdmin.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.lukitor.projectta.Model.Berita
import com.lukitor.projectta.Model.Penyakit
import com.lukitor.projectta.R
import com.squareup.picasso.Picasso

class MasterBeritaAdapter(val mCtx: Context, val layoutResId: Int, val mhsList:List<Berita>):
    ArrayAdapter<Berita>(mCtx,layoutResId,mhsList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val tvNama: TextView = view.findViewById(R.id.txtJudul)
        val tvTanggal: TextView = view.findViewById(R.id.txtTanggal)
        val tvEdit: TextView = view.findViewById(R.id.txtEdit)
        val img: ImageView = view.findViewById(R.id.imageView32)

        val mahasiswa = mhsList[position]

        tvEdit.setOnClickListener {
            showUpdateDialog(mahasiswa)
        }

        tvNama.text = mahasiswa.judul
        tvTanggal.text = mahasiswa.tanggal
        Picasso.get().load("https://i.imgur.com/XeVG9tV.jpg").into(img)

        return view
    }

    fun showUpdateDialog(mahasiswa: Berita) {
        val builder = AlertDialog.Builder(mCtx)
        builder.setTitle("Edit Data Berita")
        val inflater = LayoutInflater.from(mCtx)
        val view = inflater.inflate(R.layout.update_dialogue_berita, null)

        val etNama = view.findViewById<EditText>(R.id.etJudul)
        val etKonten = view.findViewById<EditText>(R.id.etKonten)

        etNama.setText(mahasiswa.judul)
        etKonten.setText(mahasiswa.konten)

        builder.setView(view)

        builder.setPositiveButton("Update") { p0, p1 ->
            val dbMhs = FirebaseDatabase.getInstance().getReference("BERITA")
            val nama = etNama.text.toString().trim()
            val konten = etKonten.text.toString().trim()
            val mhs = Berita(mahasiswa.id, nama,konten,mahasiswa.tanggal )
            dbMhs.child(mahasiswa.id!!).setValue(mhs).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(mCtx, "Data berita berhasil update", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mCtx, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        builder.setNegativeButton("No") { p0, p1 ->

        }

        val alert = builder.create()
        alert.show()
    }
}
