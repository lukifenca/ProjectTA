package com.lukitor.projectta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lukitor.projectta.Model.Pasien
import com.lukitor.projectta.activityDokter.HomeDokterActivity
import com.lukitor.projectta.activityDokter.RegisterDokterActivity
import com.lukitor.projectta.activityPasien.HomePasienActivity
import com.lukitor.projectta.activityPasien.ProfilePasienActivity
import com.lukitor.projectta.activityPasien.RegisterPasienActivity
import com.lukitor.projectta.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegisterPasien.setOnClickListener {
            Intent(this,RegisterPasienActivity::class.java).also{
                startActivity(it)
            }
        }

        binding.btnRegisterDokter.setOnClickListener {
            Intent(this,RegisterDokterActivity::class.java).also{
                startActivity(it)
            }

        }

        binding.btnReset.setOnClickListener {
            Intent(this,ResetPasswordActivity::class.java).also{
                startActivity(it)
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPW.text.toString().trim()
            if (email.isEmpty()){
                binding.etEmail.error = "Email harus disi"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.etEmail.error = "Email tidak valid"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty() || password.length < 6){
                binding.etPW.error = "Password harus di isi lebih dari 6 karakter"
                binding.etPW.requestFocus()
                return@setOnClickListener
            }
            loginUser(email,password)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){
            if (it.isSuccessful){
                checkrole()
            }
            else{
                Toast.makeText(this,it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart(){
        super.onStart()
        if(auth.currentUser != null){
            checkrole()
        }
    }

    private fun checkrole(){
        var role= ""
        FirebaseDatabase.getInstance().getReference("PASIEN").child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user =snapshot.getValue<Pasien>(Pasien::class.java)
                    role = user!!.role.toString().trim()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        val timer = object: CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (role != ""){
                    cancel()
                    changeact(role)
                }
            }
            override fun onFinish() { changeact(role)
            }
        }
        timer.start()

    }

    private fun changeact(role:String){
        if (role == "Pasien"){
            Intent(this,ProfilePasienActivity::class.java).also{ intent->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        else if(role == "Dokter"){
            Intent(this,HomeDokterActivity::class.java).also{ intent->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

        }
        else{

        }
    }
}