package com.lukitor.projectta.activityPasien

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.Model.Chat
import com.lukitor.projectta.Model.Dokter
import com.lukitor.projectta.Model.HistoryTransaksi
import com.lukitor.projectta.R
import com.lukitor.projectta.activityDokter.adapter.ListChatDokterAdapter
import com.lukitor.projectta.databinding.ActivityChatPasienBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ChatPasienActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityChatPasienBinding
    var hTransID = ""
    var dokterID = ""
    var nama = ""
    private lateinit var listChat : MutableList<Chat>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatPasienBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hTransID = intent.getStringExtra("id").toString()
        dokterID = intent.getStringExtra("dokterid").toString()
        nama =  intent.getStringExtra("nama").toString()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            firebaseUser = auth.currentUser!!
            listChat = mutableListOf()
            binding.txtNamaDokter.text = nama
            cekStatusKonsul()

            binding.imgbackprofiledokter.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
            val popup = PopupMenu(this, binding.imgMenu)
            popup.menu.add(Menu.NONE, 1, Menu.NONE, "Akhiri Sesi Konsultasi")
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> {// Load Image from Drawable
                        endSession()
                        finish()
                        overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
                        true
                    }
                    else -> false
                }
            }
            binding.imgMenu.setOnClickListener {
                popup.show()
            }
            binding.imgsendmsg.setOnClickListener {
                if (binding.enterMsg.text.trim().isNotEmpty()){
                    val simpleDateFormat= SimpleDateFormat("HH:mm:ss")
                    val jammulai:String = simpleDateFormat.format(Date())
                    sendChat(binding.enterMsg.text.toString(),jammulai,"Pasien")
                }
                else{
                    binding.enterMsg.error = "Pesan tidak boleh kosong"
                    binding.enterMsg.requestFocus()
                    return@setOnClickListener
                }
            }
        }
        else{
            signout()
        }

    }
    private fun sendChat(pesan: String,jamKirim: String, tipePengirim: String){
        val ref = FirebaseDatabase.getInstance().getReference("CHAT").child(hTransID)
        val historyId = ref.push().key
        val hTrans =    Chat(historyId.toString(),pesan,jamKirim,tipePengirim)
        ref.child(historyId.toString()).setValue(hTrans).addOnCompleteListener {
            binding.enterMsg.text.clear()
        }
    }
    private fun endSession(){
        val ref = FirebaseDatabase.getInstance().getReference().child("HISTORYTRANSAKSI").child(hTransID)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user =snapshot.getValue<HistoryTransaksi>(HistoryTransaksi::class.java)
                    user!!.statusTrans = 2
                    ref.setValue(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
    private fun cekStatusKonsul(){
        var simpleDateFormat= SimpleDateFormat("dd-MM-yyyy")
        val tanggal:String = simpleDateFormat.format(Date())
        val ref = FirebaseDatabase.getInstance().getReference("HISTORYTRANSAKSI")
        val query = ref.orderByChild("dokterId").equalTo(dokterID)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (i in snapshot.children){
                        val history = i.getValue(HistoryTransaksi::class.java)
                        if(history!=null){
                            if (history.id == hTransID){
                                if (history.pasienId == firebaseUser.uid && history.statusTrans == 1){
                                    binding.imageView20.visibility = View.GONE
                                    binding.txtjenispenyakitpasien.visibility = View.GONE
                                    binding.rvHistoryChat.visibility = View.VISIBLE
                                    binding.linearLayout13.visibility = View.VISIBLE
                                }
                                else if(history.pasienId == firebaseUser.uid && history.statusTrans == 0){
                                    binding.imageView20.visibility = View.VISIBLE
                                    binding.txtjenispenyakitpasien.visibility = View.VISIBLE
                                    binding.rvHistoryChat.visibility = View.GONE
                                    binding.linearLayout13.visibility = View.GONE

                                }
                                else if(history.pasienId == firebaseUser.uid && history.statusTrans == 2){
                                    binding.imageView20.visibility = View.GONE
                                    binding.txtjenispenyakitpasien.visibility = View.GONE
                                    binding.linearLayout13.visibility = View.GONE
                                    binding.rvHistoryChat.visibility = View.VISIBLE

                                }
                            }

                        }
                    }
                }
                chatInfo()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun chatInfo(){
        val ref = FirebaseDatabase.getInstance().getReference().child("CHAT").child(hTransID)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listChat.clear()
                    for (h in snapshot.children){
                        val chat = h.getValue(Chat::class.java)
                        if(chat!=null){
                            listChat.add(chat)
                        }
                    }
                    val adapter = ListChatDokterAdapter(this@ChatPasienActivity,R.layout.itemlistchatdokter,listChat,2)
                    binding.rvHistoryChat.adapter = adapter
                    binding.rvHistoryChat.setSelection(adapter.count - 1)
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
            overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
        }
    }
}