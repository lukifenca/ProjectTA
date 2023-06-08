package com.lukitor.projectta.activityAdmin.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.lukitor.projectta.Model.Obat
import com.lukitor.projectta.Model.Penyakit
import com.lukitor.projectta.R

class MasterObatAdapter (val mCtx: Context, val layoutResId: Int, val mhsList:List<Obat>):
    ArrayAdapter<Obat>(mCtx,layoutResId,mhsList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val tvNama: TextView = view.findViewById(R.id.tvNamaObat)
        val tvEdit: TextView = view.findViewById(R.id.tvEdit)

        val mahasiswa = mhsList[position]

        tvEdit.setOnClickListener {
            showUpdateDialog(mahasiswa)
        }

        tvNama.text = "Nama Obat : " + mahasiswa.namaobat

        return view
    }

    fun showUpdateDialog(mahasiswa: Obat) {
        val builder = AlertDialog.Builder(mCtx)
        builder.setTitle("Edit Data")
        val inflater = LayoutInflater.from(mCtx)
        val view = inflater.inflate(R.layout.update_dialogue_master_penyakit, null)

        val etNama = view.findViewById<EditText>(R.id.etNamaPenyakitMaster)

        etNama.setText(mahasiswa.namaobat)

        builder.setView(view)

        builder.setPositiveButton("Update") { p0, p1 ->
            val dbMhs = FirebaseDatabase.getInstance().getReference("OBAT").child(mahasiswa.idpenyakit)
            val nama = etNama.text.toString().trim()
            val mhs = Obat(mahasiswa.id, mahasiswa.idpenyakit,nama)
            dbMhs.child(mahasiswa.id!!).setValue(mhs).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(mCtx, "Data berhasil update", Toast.LENGTH_SHORT).show()
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
