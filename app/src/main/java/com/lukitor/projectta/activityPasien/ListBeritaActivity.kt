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
import com.lukitor.projectta.R
import com.lukitor.projectta.activityAdmin.adapter.MasterBeritaAdapter
import com.lukitor.projectta.activityDokter.KonsultasiOnline.ChatDokterActivity
import com.lukitor.projectta.activityPasien.adapter.ListBeritaAdapter
import com.lukitor.projectta.databinding.ActivityListBeritaBinding
import com.lukitor.projectta.databinding.ActivityTambahBeritaBinding

class ListBeritaActivity : AppCompatActivity() {
    private lateinit var listBerita : MutableList<Berita>
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityListBeritaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityListBeritaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            listBerita = mutableListOf()
            Loading()
            loadData()
            binding.imgbackprofilepasien.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
            binding.rvHistorysaldo.setOnItemClickListener { parent, view, position, id  ->
                val id = listBerita[position].id
                Intent(this, DetailBeritaActivity::class.java).also {
                    it.putExtra("id", id)
                    startActivity(it)
                }
            }


        }
        else{
            signout()
        }
    }
    private fun loadData(){
        val ref = FirebaseDatabase.getInstance().getReference().child("BERITA")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listBerita.clear()
                    for (h in snapshot.children){
                        val admin = h.getValue(Berita::class.java)
                        if(admin!=null){
                            listBerita.add(admin)
                            val adapter = ListBeritaAdapter(this@ListBeritaActivity,R.layout.itemlistberitapasien,listBerita)
                            binding.rvHistorysaldo.adapter = adapter
                        }
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
            startActivity(it)
            finish()
            overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
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
        }, 4000) // 2000 mill
    }
}