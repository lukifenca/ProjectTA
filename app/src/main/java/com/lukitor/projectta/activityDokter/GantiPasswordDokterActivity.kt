package com.lukitor.projectta.activityDokter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.R
import com.lukitor.projectta.databinding.ActivityGantiPasswordDokterBinding

class GantiPasswordDokterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityGantiPasswordDokterBinding
    private lateinit var user: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGantiPasswordDokterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            user = auth.currentUser!!
            binding.btnBackGantiPw.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
                Toast.makeText(this, "Penggantian password dibatalkan!", Toast.LENGTH_SHORT).show()
            }
            binding.btnUpdate.setOnClickListener {
                val password = binding.etPasswordChangePassword.text.toString().trim()
                val konfpassword = binding.etKonfPasswordChangePassword.text.toString().trim()

                if (password.isEmpty() || password.length < 6) {
                    binding.etPasswordChangePassword.error =
                        "Sandi harus di isi lebih dari 6 karakter"
                    binding.etPasswordChangePassword.requestFocus()
                    return@setOnClickListener
                }
                if (password != konfpassword) {
                    binding.etKonfPasswordChangePassword.error = "Konfirmasi Sandi tidak sesuai"
                    binding.etKonfPasswordChangePassword.requestFocus()
                    return@setOnClickListener
                }

                user.let {
                    user.updatePassword(password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Update Password Berhasil", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                            overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
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

    private fun signout(){
        auth.signOut()
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
        }
    }
}