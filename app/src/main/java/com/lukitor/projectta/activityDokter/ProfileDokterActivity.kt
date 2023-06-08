package com.lukitor.projectta.activityDokter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.Model.Dokter
import com.lukitor.projectta.Model.HistorySaldo
import com.lukitor.projectta.R
import com.lukitor.projectta.VerifActivity
import com.lukitor.projectta.activityDokter.KonsultasiOnline.AntrianKonsultasiActivity
import com.lukitor.projectta.activityPasien.DetailProfilePasienActivity
import com.lukitor.projectta.databinding.ActivityProfileDokterBinding
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ProfileDokterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityProfileDokterBinding
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_dokter)
        binding= ActivityProfileDokterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            if (!firebaseUser.isEmailVerified){
                Intent(this, VerifActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                    finish()
                }
            }
            userInfo()

            binding.btnHSaldo.setOnClickListener{
                Intent(this, HistorySaldoActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.dokterkelolaprofile.setOnClickListener{
                Intent(this, DetailProfileDokter::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.dokterGantiPW.setOnClickListener{
                Intent(this, GantiPasswordDokterActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.dokterGantiEmail.setOnClickListener{
                Intent(this, GantiEmailDokterActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.dokterwallet.setOnClickListener {
                Intent(this, TarikSaldoActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.dokterKonsultasiOnline.setOnClickListener{
                Intent(this, AntrianKonsultasiActivity::class.java).also{
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.imgbackprofiledokter.setOnClickListener {
                signout()
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
            overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
            finish()
        }
    }
    private fun userInfo(){
        val ref = FirebaseDatabase.getInstance().getReference().child("DOKTER").child(auth.currentUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user =snapshot.getValue<Dokter>(Dokter::class.java)
                    binding.apply {
                        val myString = NumberFormat.getInstance().format(user!!.saldo);
                        textView25.setText(myString+",-")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
    fun doubleToStringNoDecimal(d: Double): String? {
        val formatter: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,###")
        return formatter.format(d)
    }
}