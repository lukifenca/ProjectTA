package com.lukitor.projectta.activityPasien.Reports

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.Model.Admin
import com.lukitor.projectta.Model.Dokter
import com.lukitor.projectta.Model.HistoryTransaksi
import com.lukitor.projectta.R
import com.lukitor.projectta.activityDokter.KonsultasiOnline.ChatDokterActivity
import com.lukitor.projectta.activityPasien.ChatPasienActivity
import com.lukitor.projectta.databinding.ActivityChatDokterBinding
import com.lukitor.projectta.databinding.ActivityPasienDetailReportsKonsultasiBinding
import java.text.NumberFormat

class PasienDetailReportsKonsultasi : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityPasienDetailReportsKonsultasiBinding
    private lateinit var firebaseUser: FirebaseUser
    var hTransID = ""
    var iddokter = ""
    var namadokter = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPasienDetailReportsKonsultasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hTransID = intent.getStringExtra("id").toString()
        iddokter = intent.getStringExtra("iddokter").toString()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!

            Loading()
            loadData()
            binding.imgbackprofilepasien.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
            binding.btnRiwayat.setOnClickListener {
                Intent(this, ChatPasienActivity::class.java).also {
                    it.putExtra("id", hTransID)
                    it.putExtra("dokterid", iddokter)
                    it.putExtra("nama", namadokter)
                    startActivity(it)
                }
            }

        }
        else{
            signout()
        }
    }
    private  fun loadData(){
        val ref = FirebaseDatabase.getInstance().getReference().child("HISTORYTRANSAKSI").child(hTransID)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user =snapshot.getValue<HistoryTransaksi>(HistoryTransaksi::class.java)
                    binding.apply {
                        var temp = NumberFormat.getInstance().format(user!!.tarifKonsultasi+ user!!.feeKonsultasi)
                        txtTotalPembayaran.text = "Rp. $temp,-"
                        temp = NumberFormat.getInstance().format(user!!.tarifKonsultasi)
                        txtBiayaKonsultasi.text = "Rp. $temp,-"
                        temp = NumberFormat.getInstance().format(user!!.feeKonsultasi)
                        txtFee.text = "Rp. $temp,-"
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        val ref1 = FirebaseDatabase.getInstance().getReference().child("DOKTER").child(iddokter)
        ref1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user =snapshot.getValue<Dokter>(Dokter::class.java)
                    namadokter = user!!.nama.toString()
                    binding.txtNamaDokter.text = user!!.nama
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
        }, 2000) // 2000 mill
    }
}