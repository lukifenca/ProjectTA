package com.lukitor.projectta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lukitor.projectta.databinding.ActivityVerifBinding

class VerifActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityVerifBinding
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVerifBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        var ctr = 1
        val timer = object: CountDownTimer(2000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                println(ctr)
                ctr++
                firebaseUser.reload()
                if (firebaseUser.isEmailVerified){
                    cancel()
                    changeact()
                }
            }
            override fun onFinish() { changeact()
            }
        }
        timer.start()

        binding.btnSend.setOnClickListener {
            firebaseUser?.sendEmailVerification().addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this, "Email Verifikasi telah dikirim", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnLogoutverif.setOnClickListener {
            auth.signOut()
            Intent(this, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }
    private fun changeact(){
        Toast.makeText(this, "Email telah berhasil di verifikasi", Toast.LENGTH_SHORT).show()
        Intent(this, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
            finish()
        }
    }
}