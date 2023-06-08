package com.lukitor.projectta.activityAdmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
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
import com.lukitor.projectta.VerifActivity
import com.lukitor.projectta.databinding.ActivityGantiEmailAdminBinding
import com.lukitor.projectta.databinding.ActivityGantiEmailPasienBinding

class GantiEmailAdminActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityGantiEmailAdminBinding
    private lateinit var user: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGantiEmailAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            user = auth.currentUser!!
            binding.btnBackGantiemail.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
                Toast.makeText(this, "Penggantian email dibatalkan!", Toast.LENGTH_SHORT).show()
            }
            binding.btnUpdate.setOnClickListener {
                val email = binding.etEmailChangeEmail.text.toString().trim()
                if (email.isEmpty()) {
                    binding.etEmailChangeEmail.error = "Email harus disi"
                    binding.etEmailChangeEmail.requestFocus()
                    return@setOnClickListener
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.etEmailChangeEmail.error = "Email tidak valid"
                    binding.etEmailChangeEmail.requestFocus()
                    return@setOnClickListener
                }

                user.let {
                    user.updateEmail(email).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this,"Email Berhasil Ganti, Email telah di ganti dengan : " + email,Toast.LENGTH_SHORT).show()
                            user?.sendEmailVerification().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    updatData(email)
                                    Toast.makeText(this,"Email Verifikasi baru telah dikirim", Toast.LENGTH_SHORT).show()
                                    Intent(this, VerifActivity::class.java).also {
                                        it.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(it)
                                        finish()
                                        overridePendingTransition(
                                            R.transition.nothing,
                                            R.transition.bottom_down
                                        )
                                    }
                                } else {
                                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        } else {
                            Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        else{
            signout()
        }
    }
    private fun updatData(email:String){
        val ref = FirebaseDatabase.getInstance().getReference("ADMIN").child(auth.currentUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var user =snapshot.getValue<Admin>(Admin::class.java)
                user!!.email =  email
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
}