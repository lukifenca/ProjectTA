package com.lukitor.projectta.activityDokter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.Model.Dokter
import com.lukitor.projectta.Model.HistorySaldo
import com.lukitor.projectta.R
import com.lukitor.projectta.databinding.ActivityTarikSaldoBinding
import com.squareup.picasso.Picasso
import java.text.NumberFormat

class TarikSaldoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityTarikSaldoBinding
    private lateinit var firebaseUser: FirebaseUser
    var totalsaldo = 0
    var saldoakhir = 0
    private lateinit var namabank: String
    private lateinit var norek: String
    private lateinit var atasnama: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTarikSaldoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            userInfo()
            binding.imgbackprofiledokter.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
            binding.btnTarikSaldo.setOnClickListener {
                val saldotarik = binding.etTarif.text.toString().trim().toInt()
                if (totalsaldo < saldotarik) {
                    binding.etTarif.error = "Saldo yang ingin anda tarik tidak mencukupi"
                    binding.etTarif.requestFocus()
                    return@setOnClickListener
                }
                if (saldotarik < 15000) {
                    binding.etTarif.error = "Saldo yang ingin ditarik minimal adalah Rp. 15.000,-"
                    binding.etTarif.requestFocus()
                    return@setOnClickListener
                }
                if (saldotarik >= 15000 && totalsaldo > saldotarik){
                    saldoakhir = totalsaldo -saldotarik
                    UpdateAndAddHistory(namabank,norek,atasnama,totalsaldo,saldotarik,saldoakhir)
                }
            }
        }
        else{
            signout()
        }
    }
    private fun signout(){
        auth.signOut()
        Intent(this, MainActivity::class.java).also {
            overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            startActivity(it)
            finish()
        }
    }

    private fun UpdateAndAddHistory(namabank: String, norek: String, atasnama: String, totalsaldo: Int, saldotarik: Int, saldoakhir: Int){
        val ref = FirebaseDatabase.getInstance().getReference("DOKTER").child(auth.currentUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var user = snapshot.getValue<Dokter>(Dokter::class.java)
                user!!.saldo = saldoakhir
                ref.setValue(user)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        val ref1 = FirebaseDatabase.getInstance().getReference("HISTORYSALDO").child(auth.currentUser!!.uid)
        val historyId = ref1.push().key
        val hSaldo = HistorySaldo(historyId.toString(),totalsaldo,saldotarik,saldoakhir,1,1)
        ref1.child(historyId.toString()).setValue(hSaldo).addOnCompleteListener {
            Toast.makeText(this@TarikSaldoActivity, "Penarikan saldo sebesar Rp."+ saldotarik  + ",- telah berhasil.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun userInfo(){
        val ref = FirebaseDatabase.getInstance().getReference().child("DOKTER").child(auth.currentUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user =snapshot.getValue<Dokter>(Dokter::class.java)
                    val uid = firebaseUser.uid
                    binding.apply {
                        etNamaBank.setText(user!!.namabank.toString())
                        etNoRek.setText(user!!.norek.toString())
                        etAN.setText(user!!.atasnama.toString())
                        val myString = NumberFormat.getInstance().format(user!!.saldo);
                        totalnominal.setText("Rp. " +myString+ ",-")
                        totalsaldo = user!!.saldo!!.toInt()
                        namabank = user!!.namabank.toString()
                        atasnama = user!!.atasnama.toString()
                        norek = user!!.norek.toString()
//
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}