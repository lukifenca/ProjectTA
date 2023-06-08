package com.lukitor.projectta.activityPasien

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.View
import android.view.WindowManager
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
import com.lukitor.projectta.Model.Admin
import com.lukitor.projectta.Model.Dokter
import com.lukitor.projectta.Model.HistoryTransaksi
import com.lukitor.projectta.Model.Pasien
import com.lukitor.projectta.R
import com.lukitor.projectta.VerifActivity
import com.lukitor.projectta.activityDokter.HomeDokterActivity
import com.lukitor.projectta.activityPasien.Reports.PasienReportsKonsultasi
import com.lukitor.projectta.activityPasien.Reports.PasienReportsSubsActivity
import com.lukitor.projectta.databinding.ActivityHomePasienBinding
import java.text.SimpleDateFormat
import java.util.*

class HomePasienActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityHomePasienBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var listDokter : MutableList<Dokter>
    private lateinit var listTrans : MutableList<HistoryTransaksi>
    var hTransID = ""
    var dokterID = ""
    var namadokter = ""
    var status = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomePasienBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            if (!firebaseUser.isEmailVerified) {
                Intent(this, VerifActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                    finish()
                }
            }
            else{
                listDokter = mutableListOf()
                listTrans = mutableListOf()
                userInfo()
                Loading()
                cekStatusKonsultasi()
            }
            val popup = PopupMenu(this, binding.layoutReport)
            popup.menu.add(Menu.NONE, 1, Menu.NONE, "Lihat Laporan Subscription")
            popup.menu.add(Menu.NONE, 2, Menu.NONE, "Ganti Laporan Riwayat Konsultasi")
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> {
                        Intent(this, PasienReportsSubsActivity::class.java).also {
                            startActivity(it)
                            overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                        }
                        true
                    }
                    2 -> {
                        Intent(this, PasienReportsKonsultasi::class.java).also {
                            startActivity(it)
                            overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                        }
                        true
                    }
                    else -> false
                }

            }
            binding.constraintLayout5.setOnClickListener {
                Intent(this, DetailProfilePasienActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }

            }
            binding.layoutKonsul.setOnClickListener {
                cekStatusKonsultasi()
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
            binding.layoutReport.setOnClickListener {
                popup.show()
            }
            binding.layoutBerita.setOnClickListener {
                Intent(this, ListBeritaActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.layoutScan.setOnClickListener {
                Intent(this, ScanActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.layoutSubs.setOnClickListener {
                Intent(this, ListSubsActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.imgLogout.setOnClickListener {  signout() }
            binding.txtLogout.setOnClickListener {  signout() }
        }
        else{
            signout()
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
                            val ref = FirebaseDatabase.getInstance().getReference("HISTORYTRANSAKSI")
                            val query = ref.orderByChild("dokterId").equalTo(dokter.id)
                            query.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        for (i in snapshot.children){
                                            val history = i.getValue(HistoryTransaksi::class.java)
                                            if(history!=null){
                                                if (history.tanggal == tanggal){
                                                    if (history.pasienId == firebaseUser.uid &&  (history.statusTrans == 0 || history.statusTrans == 1)){
                                                        status = true
                                                        hTransID = history.id
                                                        dokterID = dokter.id
                                                        namadokter = dokter.nama.toString()
                                                    }
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
        val ref = FirebaseDatabase.getInstance().getReference().child("PASIEN").child(firebaseUser.uid)
        ref.addValueEventListener(object : ValueEventListener {
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
                    if (user!!.jeniskelamin == "Pria"){
                        binding.imageprofiledokter.setImageResource(R.drawable.ic_man);
                    }
                    else{
                        binding.imageprofiledokter.setImageResource(R.drawable.ic_woman);
                    }
                    binding.apply {
                        txtNamaHalo.text = "Halo, ${user!!.nama}"
                        tvProfilenamapasien.text = "${user!!.nama}"
                        tvProfilebbpasien .text = "${user!!.beratbadan} Kg"
                        tvProfiletbpasien.text = "${user!!.tinggibadan} Cm"
                        tvProfileusiapasien.text = "$ageS Tahun"
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