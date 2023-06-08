package com.lukitor.projectta.activityDokter.KonsultasiOnline

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.Model.Pasien
import com.lukitor.projectta.Model.Chat
import com.lukitor.projectta.R
import com.lukitor.projectta.activityDokter.adapter.ListChatDokterAdapter
import com.lukitor.projectta.databinding.ActivityChatDokterBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class ChatDokterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityChatDokterBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var listChat : MutableList<Chat>
    var hTransID = ""
    var pasien = ""
    var status = "open"
    lateinit var Uri2: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatDokterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hTransID = intent.getStringExtra("id").toString()
        pasien =  intent.getStringExtra("pasien").toString()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            firebaseUser = auth.currentUser!!
            listChat = mutableListOf()
            userInfo()
            chatInfo()

            binding.imgsendmsg.setOnClickListener {
                if (binding.enterMsg.text.trim().isNotEmpty()){
                    val simpleDateFormat= SimpleDateFormat("HH:mm:ss")
                    val jammulai:String = simpleDateFormat.format(Date())
                    sendChat(hTransID,binding.enterMsg.text.toString(),jammulai,"Dokter")
                }
                else{
                    binding.enterMsg.error = "Pesan tidak boleh kosong"
                    binding.enterMsg.requestFocus()
                    return@setOnClickListener
                }
            }

            binding.imgMenu.setOnClickListener {
                if (status=="open"){
                    binding.imgMenu.setImageResource(R.drawable.baseline_menu_24);
                    binding.imgpenyakitpasien.visibility = View.GONE
                    binding.txtjenispenyakitpasien.visibility = View.GONE
                    binding.txtGenderPasien.visibility = View.GONE
                    binding.txtUsiapasien.visibility = View.GONE
                    status = "close"
                }
                else if(status=="close"){
                    binding.imgMenu.setImageResource(R.drawable.baseline_close_24);
                    binding.imgpenyakitpasien.visibility = View.VISIBLE
                    binding.txtjenispenyakitpasien.visibility = View.VISIBLE
                    binding.txtGenderPasien.visibility = View.VISIBLE
                    binding.txtUsiapasien.visibility = View.VISIBLE
                    status = "open"
                }
            }
            binding.imgpenyakitpasien.setOnClickListener {
                val imagePopupstr2 = ImagePopup(this)
                imagePopupstr2.initiatePopupWithPicasso("https://i.imgur.com/qo7AsDy.jpg")
                binding.imgpenyakitpasien.setOnClickListener {
                    imagePopupstr2.viewPopup();
                }
            }
            binding.imgbackprofiledokter.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
        }
        else{
            signout()
        }
    }
    private fun sendChat(hTransID: String,pesan: String,jamKirim: String, tipePengirim: String){
        val ref = FirebaseDatabase.getInstance().getReference("CHAT").child(hTransID)
        val historyId = ref.push().key
        val hTrans =    Chat(historyId.toString(),pesan,jamKirim,tipePengirim)
        ref.child(historyId.toString()).setValue(hTrans).addOnCompleteListener {
            binding.enterMsg.text.clear()
        }
    }
    private fun userInfo(){
        Picasso.get().load("https://i.imgur.com/qo7AsDy.jpg").into(binding.imgpenyakitpasien)
        val ref = FirebaseDatabase.getInstance().getReference().child("PASIEN")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (h in snapshot.children){
                        val dataPasien = h.getValue(Pasien::class.java)
                        if(dataPasien!=null){
                            if (dataPasien.id == pasien){
                                binding.txtNamaPasien.text = dataPasien.nama
                                binding.txtGenderPasien.text = dataPasien.jeniskelamin
                                val dob: Calendar = Calendar.getInstance()
                                val today: Calendar = Calendar.getInstance()
                                val tgllhair = dataPasien.tanggallahir
                                val mString = tgllhair!!.split("-").toTypedArray()
                                dob.set(mString[2].toInt(),mString[1].toInt(),mString[0].toInt())
                                var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
                                if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
                                    age--
                                }
                                val ageInt = age
                                val ageS = "$ageInt Tahun"
                                binding.txtUsiapasien.text = ageS
                            }
                        }
                    }
                }
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
                    val adapter = ListChatDokterAdapter(this@ChatDokterActivity,R.layout.itemlistchatdokter,listChat,1)
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