package com.lukitor.projectta.activityAdmin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
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
import com.lukitor.projectta.R
import com.lukitor.projectta.databinding.ActivityDetailProfileAdminBinding

class DetailProfileAdminActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityDetailProfileAdminBinding
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailProfileAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            Loading()
            userInfo()
            binding.btnUpdate.setOnClickListener {
                val nama = binding.etNama.text.toString().trim()
                var telp = binding.etTelp.text.toString().trim()
                val selectedid = binding.rdgRegisPasien.checkedRadioButtonId
                var kelamin = ""
                if(binding.rbPasienPria.isChecked){
                    kelamin = "Pria"
                }
                else if(binding.rbPasienWanita.isChecked){
                    kelamin = "Wanita"
                }
                if (nama.isEmpty()) {
                    binding.etNama.error = "Nama Harus Di Isi"
                    binding.etNama.requestFocus()
                    return@setOnClickListener
                }
                if (!Patterns.PHONE.matcher(telp).matches() || telp.length < 10) {
                    binding.etTelp.error = "Nomor Telepon tidak valid"
                    binding.etTelp.requestFocus()
                    return@setOnClickListener
                }
                updatData(nama,telp,kelamin)
                Toast.makeText(this, "Perubahan data telah berhasil!", Toast.LENGTH_SHORT).show()
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
            binding.btnChangeEmail.setOnClickListener{
                Intent(this, GantiEmailAdminActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.btnChangePW.setOnClickListener {
                Intent(this, GantiPasswordAdminActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.btnBack.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
        }
        else{
            signout()
        }
    }

    private fun userInfo(){
        val ref = FirebaseDatabase.getInstance().getReference().child("ADMIN").child(auth.currentUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user =snapshot.getValue<Admin>(Admin::class.java)
                    if (user!!.jeniskelamin.toString() =="Pria"){
                        binding.rbPasienPria.isChecked = true
                        binding.rbPasienWanita.isChecked = false
                        binding.imageprofiledokter3.setImageResource(R.drawable.ic_man)
                    }
                    else{
                        binding.rbPasienPria.isChecked = false
                        binding.rbPasienWanita.isChecked = true
                        binding.imageprofiledokter3.setImageResource(R.drawable.ic_woman)
                    }

                    binding.apply {
                        etEmail.setText(user!!.email)
                        etNama.setText(user!!.nama)
                        etTelp.setText(user!!.telp.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun updatData(nama:String, telp:String,jk:String){
        val ref = FirebaseDatabase.getInstance().getReference("ADMIN").child(firebaseUser.uid)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var user =snapshot.getValue<Admin>(Admin::class.java)
                user!!.nama =  nama
                user!!.telp = telp
                user!!.jeniskelamin = jk
                ref.setValue(user)
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