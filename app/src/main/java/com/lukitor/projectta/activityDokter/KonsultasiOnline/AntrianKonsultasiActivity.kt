package com.lukitor.projectta.activityDokter.KonsultasiOnline

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.R
import com.lukitor.projectta.databinding.ActivityAntrianKonsultasiBinding
import com.lukitor.projectta.databinding.ActivityGantiEmailDokterBinding
import kotlin.math.sign


class AntrianKonsultasiActivity : AppCompatActivity() {
    lateinit var bottomNav : BottomNavigationView
    lateinit var binding: ActivityAntrianKonsultasiBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    val antrianFragment = Antrian()
    val chatFragment = Chat()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAntrianKonsultasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            user = auth.currentUser!!
            binding.imgbackprofilepasien.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
            bottomNav = binding.bottomNavigationKonsultasidokter
            loadFragment(Antrian())
            bottomNav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menuAntrian -> {
                        loadFragment(Antrian())
                        true
                    }
                    R.id.menuChat -> {
                        loadFragment(Chat())
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
        }
        else{
            signout()
        }
    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

    private fun signout(){
        auth.signOut()
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            finish()
            overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
        }
    }
}