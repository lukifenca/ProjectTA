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
import com.lukitor.projectta.R
import com.lukitor.projectta.activityAdmin.adapter.MasterObatAdapter
import com.lukitor.projectta.databinding.ActivityMasterObatBinding

class MasterObatActivity : AppCompatActivity() {
    var id = ""
    var nama = ""
    var status = 1
    private lateinit var listobat : MutableList<Obat>
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMasterObatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMasterObatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        id = intent.getStringExtra("id").toString()
        nama =  intent.getStringExtra("nama").toString()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            binding.txttitle.text = "Nama Penyakit : $nama"
            listobat = mutableListOf()
            Loading()
            loadData()
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
            binding.imgbackprofilepasien.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
            binding.btnAddDisease.setOnClickListener{
                if (binding.etNamaDisease.text.trim().isNotEmpty()){
                    val namaObat = binding.etNamaDisease.text.toString()
                    addObat(namaObat)
                }
                else{
                    binding.etNamaDisease.error = "Nama Obat Tidak Boleh Kosong"
                    binding.etNamaDisease.requestFocus()
                }
            }

        }
        else{
            signout()
        }

    }
    private fun addObat(namaObat:String){
        val ref1 = FirebaseDatabase.getInstance().getReference("OBAT").child(id)
        val historyId = ref1.push().key
        val hSaldo = Obat(historyId.toString(),id,namaObat)
        ref1.child(historyId.toString()).setValue(hSaldo).addOnCompleteListener {
            Toast.makeText(this, "Berhasil menambah jenis obat baru untuk Penyakit $nama", Toast.LENGTH_SHORT).show()
        }
        binding.etNamaDisease.text.clear()
    }
    private fun loadData(){
        val ref = FirebaseDatabase.getInstance().reference.child("OBAT").child(id)
        var ctr = 0
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listobat.clear()
                    for (h in snapshot.children){
                        val admin = h.getValue(Obat::class.java)
                        if(admin!=null){
                            ctr++
                            listobat.add(admin)
                            val adapter = MasterObatAdapter(this@MasterObatActivity,R.layout.itemlistobat,listobat)
                            binding.lvMhs.adapter = adapter
                        }
                    }
                    if (listobat.count()> 0){
                        binding.txtnohistory.visibility = View.GONE
                        binding.lvMhs.visibility = View.VISIBLE
                    }
                    else{
                        binding.txtnohistory.visibility = View.VISIBLE
                        binding.lvMhs.visibility = View.GONE
                    }
                }
                else{
                    binding.txtnohistory.visibility = View.VISIBLE
                    binding.lvMhs.visibility = View.GONE
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