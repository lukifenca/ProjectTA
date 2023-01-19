package com.lukitor.projectta.activityPasien

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.Model.Pasien
import com.lukitor.projectta.VerifActivity
import com.lukitor.projectta.databinding.ActivityProfilePasienBinding
import java.util.*

class ProfilePasienActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityProfilePasienBinding
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfilePasienBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!

        if (firebaseUser != null){
            if (!firebaseUser.isEmailVerified){
                    Intent(this, VerifActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                    finish()
                }
            }
            userInfo()

            binding.constraintLayoutprofilepasien.setOnClickListener{
                Intent(this, DetailProfilePasienActivity::class.java).also {
                    startActivity(it)
                }
            }
            binding.pasienHistoryTransaksi.setOnClickListener{

            }
            binding.pasienKelolaSubs.setOnClickListener{

            }
            binding.pasienGantiPW.setOnClickListener{
                Intent(this, GantiPasswordPasienActivity::class.java).also {
                    startActivity(it)
                }
            }
            binding.pasienGantiEmail.setOnClickListener{
                Intent(this, GantiEmailPasienActivity::class.java).also {
                    startActivity(it)
                }
            }
            binding.imgbackprofilepasien.setOnClickListener {
                auth.signOut()
                Intent(this, MainActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        else{
            auth.signOut()
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }
    }
    private fun signout(){
        auth.signOut()
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun userInfo(){
        val ref = FirebaseDatabase.getInstance().getReference().child("PASIEN").child(auth.currentUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user =snapshot.getValue<Pasien>(Pasien::class.java)
                    val dob: Calendar = Calendar.getInstance()
                    val today: Calendar = Calendar.getInstance()
                    val tgllhair = user!!.tanggallahir
                    val mString = tgllhair!!.split("-").toTypedArray()
                    dob.set(mString[2].toInt(),mString[1].toInt(),mString[0].toInt())
                    var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
                    if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
                        age--
                    }
                    val ageInt = age
                    val ageS = ageInt.toString()
                    binding.apply {
                        tvProfilenamapasien.setText(user!!.nama)
                        tvProfilebbpasien.setText(user!!.beratbadan.toString() + " Kg")
                        tvProfiletbpasien.setText(user!!.tinggibadan.toString() + " Cm")
                        tvProfileusiapasien.setText(ageS + " Tahun")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}