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
import com.lukitor.projectta.Model.Dokter
import com.lukitor.projectta.R
import com.lukitor.projectta.activityAdmin.adapter.ListAdminAdapter
import com.lukitor.projectta.databinding.ActivityListAdminBinding

class ListAdminActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityListAdminBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var listAdmin : MutableList<Admin>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            listAdmin = mutableListOf()
            Loading()
            binding.imgAddAdmin.setOnClickListener {moveintent() }
            binding.txtAddAdmin.setOnClickListener{moveintent() }
            binding.imgbackprofilepasien.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
            loadUser()

        }
        else{
            signout()
        }
    }

    private fun loadUser(){
        val ref = FirebaseDatabase.getInstance().getReference().child("ADMIN")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listAdmin.clear()
                    for (h in snapshot.children){
                        val admin = h.getValue(Admin::class.java)
                        if(admin!=null){
                            if (admin.role != "MasterAdmin"){
                                listAdmin.add(admin)
                            }
                            val adapter = ListAdminAdapter(this@ListAdminActivity,R.layout.itemlistadmin,listAdmin)
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
        }, 2000) // 2000 mill
    }

    private fun moveintent(){
        Intent(this, RegisterAdminActivity::class.java).also {
            startActivity(it)
            overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
        }
    }
}