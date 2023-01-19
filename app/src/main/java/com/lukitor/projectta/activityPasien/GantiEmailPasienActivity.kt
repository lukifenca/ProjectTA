package com.lukitor.projectta.activityPasien

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.lukitor.projectta.VerifActivity
import com.lukitor.projectta.databinding.ActivityGantiEmailPasienBinding

class GantiEmailPasienActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityGantiEmailPasienBinding
    private lateinit var user: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGantiEmailPasienBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        binding.layoutEmail.visibility = View.GONE

        binding.btnBackGantiemail.setOnClickListener {
            finish()
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
                    if (it.isSuccessful){
                        Toast.makeText(this, "Email Berhasil Ganti, Email telah di ganti dengan : " + email, Toast.LENGTH_SHORT).show()
                        user?.sendEmailVerification().addOnCompleteListener {
                            if (it.isSuccessful){
                                Toast.makeText(this, "Email Verifikasi baru telah dikirim", Toast.LENGTH_SHORT).show()
                                Intent(this, VerifActivity::class.java).also {
                                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(it)
                                    finish()
                                }
                            } else{
                                Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else{
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}