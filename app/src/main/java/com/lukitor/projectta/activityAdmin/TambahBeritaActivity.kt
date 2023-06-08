package com.lukitor.projectta.activityAdmin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.Model.Berita
import com.lukitor.projectta.R
import com.lukitor.projectta.activityAdmin.adapter.MasterBeritaAdapter
import com.lukitor.projectta.databinding.ActivityTambahBeritaBinding
import java.text.SimpleDateFormat
import java.util.*

class TambahBeritaActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityTambahBeritaBinding
    var judul = ""
    var konten = ""
    var status = 1
    private lateinit var listBerita : MutableList<Berita>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTambahBeritaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            listBerita = mutableListOf()
            Loading()
            loadData()
            binding.imgbackprofilepasien.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
            binding.imgAddDisease.setOnClickListener {
                if (status == 1){
                    binding.layoutAdd.visibility = View.VISIBLE
                    binding.imgAddDisease.setImageResource(R.drawable.closeicon)
                    status = 2
                }
                else{
                    binding.imgAddDisease.setImageResource(R.drawable.add)
                    binding.layoutAdd.visibility = View.GONE
                    status = 1
                }
            }
            binding.btnAddBerita.setOnClickListener{
                if (binding.etJudul.text.trim().isNotEmpty()){
                    judul = binding.etJudul.text.toString()
                }
                else{
                    binding.etJudul.error = "Judul Berita Tidak Boleh Kosong"
                    binding.etJudul.requestFocus()
                }
                if (binding.etKonten.text.trim().isNotEmpty()){
                    konten = binding.etKonten.text.toString()
                }
                else{
                    binding.etKonten.error = "Konten Berita Tidak Boleh Kosong"
                    binding.etKonten.requestFocus()
                }
                addData(judul,konten)
            }

        }
        else{
            signout()
        }
    }
    private fun addData(judul:String, konten:String){
        var simpleDateFormat= SimpleDateFormat("dd-MM-yyyy")
        val tanggal:String = simpleDateFormat.format(Date())
        val ref1 = FirebaseDatabase.getInstance().getReference("BERITA")
        val historyId = ref1.push().key
        val hSaldo = Berita(historyId.toString(),judul,konten,tanggal)
        ref1.child(historyId.toString()).setValue(hSaldo).addOnCompleteListener {
            Toast.makeText(this, "Berhasil menambah berita baru dengan Judul $judul", Toast.LENGTH_SHORT).show()
        }
        binding.etJudul.text.clear()
        binding.etKonten.text.clear()
    }
    private fun loadData(){
        val ref = FirebaseDatabase.getInstance().getReference().child("BERITA")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listBerita.clear()
                    for (h in snapshot.children){
                        val admin = h.getValue(Berita::class.java)
                        if(admin!=null){
                            listBerita.add(admin)
                            val adapter = MasterBeritaAdapter(this@TambahBeritaActivity,R.layout.itemlistberita,listBerita)
                            binding.rvHistorysaldo.adapter = adapter
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun signout(){
        auth.signOut()
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            finish()
            overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
        }
    }
    private fun Loading(){
        binding.mainLayout.visibility = View.GONE
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        Handler().postDelayed({
            progressDialog.dismiss()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            binding.mainLayout.visibility = View.VISIBLE
        }, 4000) // 2000 mill
    }


}