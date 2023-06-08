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
import com.lukitor.projectta.Model.Obat
import com.lukitor.projectta.Model.Paket
import com.lukitor.projectta.Model.Penyakit
import com.lukitor.projectta.R
import com.lukitor.projectta.activityAdmin.adapter.MasterObatAdapter
import com.lukitor.projectta.activityAdmin.adapter.MasterPaketAdapter
import com.lukitor.projectta.databinding.ActivityMasterPenyakitBinding
import com.lukitor.projectta.databinding.ActivityMasterSubsBinding

class MasterSubsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityMasterSubsBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var listSubs : MutableList<Paket>
    var namapaket = ""
    var hargapaket = 0
    var deteksi = 0
    var jumlahvocer = 0
    var nominal = 0
    var durasi = 0
    var status = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMasterSubsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            listSubs = mutableListOf()
            Loading()
            loadData()
            binding.imgAddAdmin.setOnClickListener {
                if (status == 1){
                    binding.layoutAddData.visibility = View.VISIBLE
                    binding.imgAddAdmin.setImageResource(R.drawable.closeicon)
                    status = 2
                }
                else{
                    binding.imgAddAdmin.setImageResource(R.drawable.add)
                    binding.layoutAddData.visibility = View.GONE
                    status = 1
                }
            }
            binding.imgbackprofilepasien.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
            binding.btnAddDisease2.setOnClickListener{
                if (binding.etNamaPaket.text.trim().isNotEmpty()){
                    namapaket = binding.etNamaPaket.text.toString()
                }
                else{
                    binding.etNamaPaket.error = "Nama Paket Tidak Boleh Kosong"
                    binding.etNamaPaket.requestFocus()
                    return@setOnClickListener
                }

                if (binding.etHargaPaket.text.trim().isNotEmpty()){
                    hargapaket = binding.etHargaPaket.text.toString().toInt()
                }
                else{
                    binding.etHargaPaket.error = "Harga Paket Tidak Boleh Kosong"
                    binding.etHargaPaket.requestFocus()
                    return@setOnClickListener
                }
                if (binding.etDeteksi.text.trim().isNotEmpty()){
                    deteksi = binding.etDeteksi.text.toString().toInt()
                }
                else{
                    binding.etDeteksi.error = "Jumlah Deteksi Tidak Boleh Kosong"
                    binding.etDeteksi.requestFocus()
                    return@setOnClickListener
                }
                if (binding.etVocerDiskon.text.trim().isNotEmpty()){
                    jumlahvocer = binding.etVocerDiskon.text.toString().toInt()
                }
                else{
                    binding.etVocerDiskon.error = "Jumlah Voucher  Tidak Boleh Kosong"
                    binding.etVocerDiskon.requestFocus()
                    return@setOnClickListener
                }
                if (binding.etNominalVocer.text.trim().isNotEmpty()){
                    nominal = binding.etNominalVocer.text.toString().toInt()
                }
                else{
                    binding.etNominalVocer.error = "Nominal Voucher Tidak Boleh Kosong"
                    binding.etNominalVocer.requestFocus()
                    return@setOnClickListener
                }
                if (binding.etDurasi.text.trim().isNotEmpty()){
                    durasi  = binding.etDurasi.text.toString().toInt()
                }
                else{
                    binding.etDurasi.error = "Durasi Paket Tidak Boleh Kosong"
                    binding.etDurasi.requestFocus()
                    return@setOnClickListener
                }
                addData(namapaket,hargapaket,durasi, deteksi,jumlahvocer,nominal)
            }

        }
        else{
            signout()
        }
    }
    private fun addData(namapaket:String, hargapaket:Int,durasi:Int, detek:Int, jumlahvocer:Int,nominal:Int){
        val ref1 = FirebaseDatabase.getInstance().getReference("PAKET")
        val historyId = ref1.push().key
        val hSaldo = Paket(historyId.toString(),namapaket,hargapaket,durasi,detek,jumlahvocer,nominal)
        ref1.child(historyId.toString()).setValue(hSaldo).addOnCompleteListener {
            Toast.makeText(this, "Berhasil Menambah Paket Baru", Toast.LENGTH_SHORT).show()
        }
        binding.etNamaPaket.text.clear()
        binding.etHargaPaket.text.clear()
        binding.etDurasi.text.clear()
        binding.etDeteksi.text.clear()
        binding.etVocerDiskon.text.clear()
        binding.etNominalVocer.text.clear()
    }

    private fun loadData(){
        val ref = FirebaseDatabase.getInstance().reference.child("PAKET")
        var ctr = 0
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listSubs.clear()
                    for (h in snapshot.children){
                        val admin = h.getValue(Paket::class.java)
                        if(admin!=null){
                            ctr++
                            listSubs.add(admin)
                            val adapter = MasterPaketAdapter(this@MasterSubsActivity,R.layout.itemlistsubs,listSubs)
                            binding.rvHistorysaldo.adapter = adapter
                        }
                    }
                    if (listSubs.count()> 0){
                        binding.txtnohistory.visibility = View.GONE
                        binding.rvHistorysaldo.visibility = View.VISIBLE
                    }
                    else{
                        binding.txtnohistory.visibility = View.VISIBLE
                        binding.rvHistorysaldo.visibility = View.GONE
                    }
                }
                else{
                    binding.txtnohistory.visibility = View.VISIBLE
                    binding.rvHistorysaldo.visibility = View.GONE
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