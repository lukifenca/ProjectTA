package com.lukitor.projectta.activityPasien.Reports

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
import com.lukitor.projectta.Model.HistoryPaket
import com.lukitor.projectta.Model.Paket
import com.lukitor.projectta.R
import com.lukitor.projectta.activityPasien.adapter.HistoryPaketAdapter
import com.lukitor.projectta.activityPasien.adapter.ListPaketAdapter
import com.lukitor.projectta.databinding.ActivityListSubsBinding
import com.lukitor.projectta.databinding.ActivityPasienReportsSubsBinding

class PasienReportsSubsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityPasienReportsSubsBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var listSubs : MutableList<HistoryPaket>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPasienReportsSubsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            listSubs = mutableListOf()
            Loading()
            loadData()
            binding.imgbackprofilepasien.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }

        }
        else{
            signout()
        }
    }

    private fun loadData(){
        val ref = FirebaseDatabase.getInstance().reference.child("HISTORYPAKET")
        var ctr = 0
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listSubs.clear()
                    for (h in snapshot.children){
                        val admin = h.getValue(HistoryPaket::class.java)
                        if(admin!=null){
                            ctr++
                            listSubs.add(admin)
                            val adapter = HistoryPaketAdapter(this@PasienReportsSubsActivity,R.layout.itemlistsubs,listSubs)
                            binding.rvHistorysaldo.adapter = adapter
                        }
                    }
                    if (listSubs.count()> 0){
                        binding.txtnohistory.visibility = View.GONE
                        binding.rvHistorysaldo.visibility = View.VISIBLE
                    }
                    else{
                        binding.txtnohistory.visibility = View.VISIBLE
                        binding.rvHistorysaldo.visibility = View.GONE
                    }
                }
                else{
                    binding.txtnohistory.visibility = View.VISIBLE
                    binding.rvHistorysaldo.visibility = View.GONE
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
        }, 2000) // 2000 mill
    }
}