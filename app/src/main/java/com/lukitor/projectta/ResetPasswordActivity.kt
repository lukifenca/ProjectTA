package com.lukitor.projectta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.lukitor.projectta.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityResetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBackResetPw.setOnClickListener {
            finish()
            overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            Toast.makeText(this, "Reset password dibatalkan!", Toast.LENGTH_SHORT).show()
        }
        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmailReset.text.toString().trim()
            if (email.isEmpty()) {
                binding.etEmailReset.error = "Email harus disi"
                binding.etEmailReset.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmailReset.error = "Email tidak valid"
                binding.etEmailReset.requestFocus()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this,"Cek email untuk reset password", Toast.LENGTH_SHORT).show()
                    finish()
                    overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
                }
                else{
                    Toast.makeText(this,it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}