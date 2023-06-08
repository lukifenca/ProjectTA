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
import com.lukitor.projectta.Model.Dokter
import com.lukitor.projectta.Model.HistoryTransaksi
import com.lukitor.projectta.Model.Pasien
import com.lukitor.projectta.R
import com.lukitor.projectta.VerifActivity
import com.lukitor.projectta.activityPasien.adapter.ListDokterKonsultasiAdapter
import com.lukitor.projectta.databinding.ActivityProfilePasienBinding
import java.text.SimpleDateFormat
import java.util.*

class ProfilePasienActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityProfilePasienBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var listDokter : MutableList<Dokter>
    private lateinit var listTrans : MutableList<HistoryTransaksi>
    var hTransID = ""
    var dokterID = ""
    var namadokter = ""
    var status = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfilePasienBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listDokter = mutableListOf()
        listTrans = mutableListOf()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            if (!firebaseUser.isEmailVerified){
                    Intent(this, VerifActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                    overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
                    finish()
                }
            }
            userInfo()
            cekStatusKonsultasi()
            binding.constraintLayoutprofilepasien.setOnClickListener{
                Intent(this, DetailProfilePasienActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.pasienHistoryTransaksi.setOnClickListener{

            }
            binding.pasienKelolaSubs.setOnClickListener{

            }
            binding.pasienKonsultasiOnline.setOnClickListener {
                if (status){
                    Intent(this, ChatPasienActivity::class.java).also {
                        it.putExtra("id", hTransID)
                        it.putExtra("dokterid", dokterID)
                        it.putExtra("nama", namadokter)
                        startActivity(it)
                        overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                    }
                }
                else{
                    Intent(this, KonsultasiOnlineActivity::class.java).also {
                        startActivity(it)
                        overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                    }
                }
            }
            binding.pasienGantiPW.setOnClickListener{
                Intent(this, GantiPasswordPasienActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.pasienGantiEmail.setOnClickListener{
                Intent(this, GantiEmailPasienActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.imgbackprofilepasien.setOnClickListener {
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
            overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
        }
    }

    private fun cekStatusKonsultasi(){
        var simpleDateFormat= SimpleDateFormat("dd-MM-yyyy")
        val tanggal:String = simpleDateFormat.format(Date())
        val ref1 = FirebaseDatabase.getInstance().getReference().child("DOKTER")
        ref1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listDokter.clear()
                    for (h in snapshot.children){
                        val dokter = h.getValue(Dokter::class.java)
                        if(dokter!=null){
                            val ref = FirebaseDatabase.getInstance().getReference("HISTORYTRANSAKSI").child(tanggal).child(dokter.id)
                            ref.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        for (i in snapshot.children){
                                            val history = i.getValue(HistoryTransaksi::class.java)
                                            if(history!=null){
                                                if (history.pasienId == firebaseUser.uid && history.statusTrans == 0){
                                                    status = true
                                                    hTransID = history.id
                                                    dokterID = dokter.id
                                                    namadokter = dokter.nama.toString()
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
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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