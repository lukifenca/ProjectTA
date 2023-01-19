package com.lukitor.projectta.activityPasien

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.Model.Pasien
import com.lukitor.projectta.VerifActivity
import com.lukitor.projectta.activityDokter.HomeDokterActivity
import com.lukitor.projectta.databinding.ActivityHomePasienBinding

class HomePasienActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityHomePasienBinding
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomePasienBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!

        auth.signOut()


        if (firebaseUser != null) {
            if (!firebaseUser.isEmailVerified) {
                Intent(this, VerifActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                    finish()
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