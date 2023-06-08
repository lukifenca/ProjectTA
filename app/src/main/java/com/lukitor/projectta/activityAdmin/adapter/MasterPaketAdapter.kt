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
import com.lukitor.projectta.Model.Paket
import com.lukitor.projectta.Model.Penyakit
import com.lukitor.projectta.R
import java.text.NumberFormat

class MasterPaketAdapter (val mCtx: Context, val layoutResId: Int, val mhsList:List<Paket>):
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
        val tvEdit: TextView = view.findViewById(R.id.txtEdit)

        val mahasiswa = mhsList[position]

        tvEdit.setOnClickListener {
            showUpdateDialog(mahasiswa)
        }

        tvNama.text = "Paket " + mahasiswa.namaPaket
        tvDeteksi.text = "Deteksi : " + mahasiswa.jmldeteksi
        tvVocer.text = "Voucher : " + mahasiswa.jmlvocer
        var hrgdiskon = NumberFormat.getInstance().format(mahasiswa.nominalvocer)
        tvDiskon.text = "Diskon : Rp. " + hrgdiskon + ",-"
        hrgdiskon = NumberFormat.getInstance().format(mahasiswa.harga)
        tvHarga.text = "Harga : Rp. "  + hrgdiskon + ",-"
        tvDurasi.text = "Durasi : " + mahasiswa.durasi + " Hari"

        return view
    }

    fun showUpdateDialog(mahasiswa: Paket) {
        val builder = AlertDialog.Builder(mCtx)
        builder.setTitle("Edit Data Paket")
        val inflater = LayoutInflater.from(mCtx)
        val view = inflater.inflate(R.layout.update_dialogue_paket, null)

        val etNamaPaket = view.findViewById<EditText>(R.id.etNamaPaket)
        val etHargaPaket = view.findViewById<EditText>(R.id.etHargaPaket)
        val etDurasi = view.findViewById<EditText>(R.id.etDurasi)
        val etDeteksi = view.findViewById<EditText>(R.id.etDeteksi)
        val etVocerDiskon = view.findViewById<EditText>(R.id.etVocerDiskon)
        val etNominalVocer = view.findViewById<EditText>(R.id.etNominalVocer)

        etNamaPaket.setText(mahasiswa.namaPaket)
        etHargaPaket.setText(mahasiswa.harga.toString())
        etDurasi.setText(mahasiswa.durasi.toString())
        etDeteksi.setText(mahasiswa.jmldeteksi.toString())
        etVocerDiskon.setText(mahasiswa.jmlvocer.toString())
        etNominalVocer.setText(mahasiswa.nominalvocer.toString())

        builder.setView(view)

        builder.setPositiveButton("Update") { p0, p1 ->
            val dbMhs = FirebaseDatabase.getInstance().getReference("PAKET")
            val namapaket = etNamaPaket.text.toString().trim()
            var hargapaket = etHargaPaket.text.toString().trim().toInt()
            var durasi = etDurasi.text.toString().trim().toInt()
            var deteksi = etDeteksi.text.toString().trim().toInt()
            var jumlahvocer = etVocerDiskon.text.toString().trim().toInt()
            var nominal = etNominalVocer.text.toString().trim().toInt()
            val mhs = Paket(mahasiswa.id, namapaket,hargapaket,durasi,deteksi,jumlahvocer,nominal)
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
