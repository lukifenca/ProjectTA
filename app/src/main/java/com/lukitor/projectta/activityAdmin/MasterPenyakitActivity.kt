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
import com.lukitor.projectta.Model.Admin
import com.lukitor.projectta.Model.Dokter
import com.lukitor.projectta.Model.HistorySaldo
import com.lukitor.projectta.Model.Penyakit
import com.lukitor.projectta.R
import com.lukitor.projectta.activityAdmin.adapter.ListAdminAdapter
import com.lukitor.projectta.activityAdmin.adapter.MasterPenyakitAdapter
import com.lukitor.projectta.databinding.ActivityListAdminBinding
import com.lukitor.projectta.databinding.ActivityMasterPenyakitBinding

class MasterPenyakitActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityMasterPenyakitBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var listPenyakit : MutableList<Penyakit>
    var status = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMasterPenyakitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            listPenyakit = mutableListOf()
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
                    val namaPenyakit = binding.etNamaDisease.text.toString()
                    addPenyakit(namaPenyakit)
                }
                else{
                    binding.etNamaDisease.error = "Nama Penyakit Tidak Boleh Kosong"
                    binding.etNamaDisease.requestFocus()
                }
            }
            //loadUser()
            binding.lvMhs.setOnItemClickListener { parent, view, position, id ->
                val mahasiswa = listPenyakit.get(position)
                Intent(this, MasterObatActivity::class.java).also {
                    it.putExtra("id", mahasiswa.id)
                    it.putExtra("nama", mahasiswa.namaPenyakit)
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }

            }

        }
        else{
            signout()
        }
    }
    private fun addPenyakit(namaPenyakit:String){
        val ref1 = FirebaseDatabase.getInstance().getReference("PENYAKIT")
        val historyId = ref1.push().key
        val hSaldo = Penyakit(historyId.toString(),namaPenyakit)
        ref1.child(historyId.toString()).setValue(hSaldo).addOnCompleteListener {
            Toast.makeText(this, "Berhasil Menambah Jenis Penyakit Baru", Toast.LENGTH_SHORT).show()
        }
        binding.etNamaDisease.text.clear()
    }
    private fun loadData(){
        val ref = FirebaseDatabase.getInstance().getReference().child("PENYAKIT")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listPenyakit.clear()
                    for (h in snapshot.children){
                        val admin = h.getValue(Penyakit::class.java)
                        if(admin!=null){
                            listPenyakit.add(admin)
                            val adapter = MasterPenyakitAdapter(this@MasterPenyakitActivity,R.layout.itemlistpenyakit,listPenyakit)
                            binding.lvMhs.adapter = adapter
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
        }, 2000) // 2000 mill
    }
}