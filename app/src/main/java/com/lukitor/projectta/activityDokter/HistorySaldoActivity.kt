package com.lukitor.projectta.activityDokter

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.Model.HistorySaldo
import com.lukitor.projectta.R
import com.lukitor.projectta.activityDokter.adapter.HistorySaldoAdapter
import com.lukitor.projectta.databinding.ActivityHistorySaldoBinding

class HistorySaldoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityHistorySaldoBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var historySaldoList : MutableList<HistorySaldo>
    var cari = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHistorySaldoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            historySaldoList = mutableListOf()
            userInfo()
            binding.imgbackprofilepasien.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
            val popup = PopupMenu(this, binding.imgmenu)
            popup.menu.add(Menu.NONE, 1, Menu.NONE, "Tampilkan Seluruh Riwayat Saldo")
            popup.menu.add(Menu.NONE, 2, Menu.NONE, "Tampilkan Riwayat Penarikan Saldo")
            popup.menu.add(Menu.NONE, 3, Menu.NONE, "Tampilkan Riwayat Penambahan Saldo")
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> {
                        cari = 0
                        historySaldoList = mutableListOf()
                        userInfo()
                        true
                    }
                    2 -> {
                        cari = 1
                        historySaldoList = mutableListOf()
                        userInfo()
                        true
                    }
                    3 -> {
                        cari = 2
                        historySaldoList = mutableListOf()
                        userInfo()
                        true
                    }
                    else -> false
                }
            }
            binding.imgmenu.setOnClickListener {
                popup.show()
            }
        }
        else{
            signout()
        }
    }

    private fun userInfo(){
        val ref = FirebaseDatabase.getInstance().getReference().child("HISTORYSALDO").child(auth.currentUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    historySaldoList.clear()
                    var ctr = 0
                    for (h in snapshot.children){
                        val historySaldo = h.getValue(HistorySaldo::class.java)
                        if(historySaldo!=null){
                            if (cari == 0){
                                ctr++
                                binding.txttitle.text = "Daftar Seluruh Riwayat Saldo"
                                historySaldoList.add(historySaldo)
                            }
                            else if (cari == 1){
                                binding.txttitle.text = "Daftar Seluruh Penarikan Saldo"
                                if (historySaldo.tipe == 1){
                                    ctr++
                                    historySaldoList.add(historySaldo)
                                }
                            }
                            else if (cari ==2){
                                binding.txttitle.text = "Daftar Seluruh Penambahan Saldo"
                                if (historySaldo.tipe == 0){
                                    ctr++
                                    historySaldoList.add(historySaldo)
                                }
                            }
                        }
                    }
                    if (ctr> 0){
                        binding.txtnohistory.visibility = View.GONE
                        binding.rvHistorysaldo.visibility = View.VISIBLE
                    }
                    else{
                        binding.txtnohistory.visibility = View.VISIBLE
                        binding.rvHistorysaldo.visibility = View.GONE
                    }
                    val adapter = HistorySaldoAdapter(this@HistorySaldoActivity,com.lukitor.projectta.R.layout.itemhistorysaldo,historySaldoList,cari)
                    binding.rvHistorysaldo.adapter = adapter
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
        }
    }
}