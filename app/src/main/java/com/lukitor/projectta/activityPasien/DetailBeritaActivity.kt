package com.lukitor.projectta.activityPasien

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.Model.Berita
import com.lukitor.projectta.Model.Pasien
import com.lukitor.projectta.R
import com.lukitor.projectta.VerifActivity
import com.lukitor.projectta.databinding.ActivityDetailBeritaBinding
import com.lukitor.projectta.databinding.ActivityHomePasienBinding
import com.squareup.picasso.Picasso
import java.util.*

class DetailBeritaActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityDetailBeritaBinding
    private lateinit var firebaseUser: FirebaseUser
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailBeritaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        id =  intent.getStringExtra("id").toString()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            Picasso.get().load("https://i.imgur.com/XeVG9tV.jpg").into(binding.imgBerita)
            beritaData()
            Loading()
            binding.imgbackprofiledokter.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
        }
        else{
            signout()
        }
    }
    private fun beritaData(){
        val ref = FirebaseDatabase.getInstance().getReference().child("BERITA").child(id)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user =snapshot.getValue<Berita>(Berita::class.java)
                    binding.apply {
                        txtJudul.text = "${user!!.judul}"
                        txtKonten.text = "${user!!.konten}"
                        txtTanggal .text = "${user!!.tanggal}"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun signout(){
        auth.signOut()
        Intent(this, MainActivity::class.java).also {
            overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            startActivity(it)
        }
    }
    private fun Loading(){
        binding.mainLayout.visibility = View.GONE
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        Handler().postDelayed({
            progressDialog.dismiss()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            binding.mainLayout.visibility = View.VISIBLE
        }, 2000) // 5000 mill
    }
}