package com.lukitor.projectta.activityAdmin

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
import com.lukitor.projectta.R
import com.lukitor.projectta.VerifActivity
import com.lukitor.projectta.databinding.ActivityHomeAdminBinding

class HomeAdminActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    lateinit var binding: ActivityHomeAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            if (!firebaseUser.isEmailVerified){
                Intent(this, VerifActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                    overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
                    finish()
                }
            }
            else{
                userInfo()
                Loading()
            }

            binding.txtDetailProfileAdmin.setOnClickListener {
                Intent(this, DetailProfileAdminActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.layoutMasterAdmin.setOnClickListener {
                Intent(this, ListAdminActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.layoutMastePasien.setOnClickListener {
                Intent(this, MasterPasienActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.layoutMasterPenyakit.setOnClickListener {
                Intent(this, MasterPenyakitActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.layoutMasterSubs.setOnClickListener {
                Intent(this, MasterSubsActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.layoutMasterBerita.setOnClickListener {
                Intent(this, TambahBeritaActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.imgLogout.setOnClickListener {  signout() }
            binding.txtLogout.setOnClickListener {  signout() }

        }
        else{
            signout()
        }
    }
    private fun signout(){
        auth.signOut()
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            finish()
            overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
        }
    }

    private fun userInfo(){
        val ref = FirebaseDatabase.getInstance().getReference().child("ADMIN").child(firebaseUser.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user =snapshot.getValue<Admin>(Admin::class.java)
                    if (user!!.role != "MasterAdmin"){
                        binding.layoutMasterAdmin.visibility = View.GONE
                    }
                    if (user!!.jeniskelamin == "Pria"){
                        binding.imageprofiledokter.setImageResource(R.drawable.ic_man);
                    }
                    else{
                        binding.imageprofiledokter.setImageResource(R.drawable.ic_woman);
                    }
                    binding.apply {
                        txtNamaHalo.text = "Halo, ${user!!.nama}"
                        tvProfilenamapasien.text = "${user!!.nama}"
                        tvHpadmin.text = "${user!!.telp}"
                        tvEmailadmin.text = "${user!!.email}"
                        tvProfilegenderadmin.text = "${user!!.jeniskelamin}"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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
        }, 2000) // 5000 mill
    }
}