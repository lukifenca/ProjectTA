package com.lukitor.projectta.activityPasien

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.Toast
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.Model.HistoryTransaksi
import com.lukitor.projectta.Model.Pasien
import com.lukitor.projectta.R
import com.lukitor.projectta.VerifActivity
import com.lukitor.projectta.activityAdmin.GantiEmailAdminActivity
import com.lukitor.projectta.activityAdmin.GantiPasswordAdminActivity
import com.lukitor.projectta.databinding.ActivityDetailProfilePasienBinding
import java.text.SimpleDateFormat
import java.util.*

class DetailProfilePasienActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityDetailProfilePasienBinding
    private lateinit var firebaseUser: FirebaseUser

    var day = 0
    var month = 0
    var year = 0
    var savedday = 0
    var savedmonth = 0
    var savedyear = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailProfilePasienBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            Loading()
            userInfo()
            binding.btnPasienDate4.setOnClickListener {
                getDateCalendar()
                DatePickerDialog(this,this,year,month,day).show()
            }
            binding.btnUpdate.setOnClickListener{
                val nama = binding.etNama.text.toString().trim()
                var temptb = binding.etTB.text.toString().trim()
                var tempbb = binding.etBB.text.toString().trim()
                val date = binding.etPasienTanggal4.text.toString().trim()
                val selectedid = binding.rdgRegisPasien.checkedRadioButtonId
                var kelamin = ""
                val rb : RadioButton = findViewById(selectedid)
                if(binding.rbPasienPria.isChecked){
                    kelamin = "Pria"
                }
                else if(binding.rbPasienWanita.isChecked){
                kelamin = "Wanita"
            }
                if (nama.isEmpty()) {
                    binding.etNama.error = "Nama Harus Di Isi"
                    binding.etNama.requestFocus()
                    return@setOnClickListener
                }
                var tb = 0
                var bb = 0
                if (temptb.isEmpty()) {
                    binding.etTB.error = "Tinggi Badan Harus Di Isi"
                    binding.etTB.requestFocus()
                    return@setOnClickListener
                }
                else{
                    tb = temptb.toInt()
                }
                if (tempbb.isEmpty()) {
                    binding.etBB.error = "Berat Badan Harus Di Isi"
                    binding.etBB.requestFocus()
                    return@setOnClickListener
                }
                else{
                    bb = tempbb.toInt()
                }
                if (date.isEmpty()) {
                    binding.etPasienTanggal4.error = "Tanggal Lahir Harus Di Isi"
                    binding.etPasienTanggal4.requestFocus()
                    return@setOnClickListener
                }
                updatData(nama,tb,bb,date,kelamin)
                Toast.makeText(this, "Perubahan data telah berhasil!", Toast.LENGTH_SHORT).show()
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
            binding.btnChangeEmail.setOnClickListener{
                Intent(this, GantiEmailPasienActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.btnChangePW.setOnClickListener {
                Intent(this, GantiPasswordPasienActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.transition.bottom_up, R.transition.nothing);
                }
            }
            binding.btnBack.setOnClickListener {
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }
        }
        else{
            signout()
        }
    }
    private fun userInfo(){
        val ref = FirebaseDatabase.getInstance().getReference().child("PASIEN").child(auth.currentUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user =snapshot.getValue<Pasien>(Pasien::class.java)
                    if (user!!.jeniskelamin.toString() =="Pria"){
                        binding.rbPasienPria.isChecked = true
                        binding.rbPasienWanita.isChecked = false
                        binding.imageprofiledokter2.setImageResource(R.drawable.ic_man);
                    }
                    else{
                        binding.rbPasienPria.isChecked = false
                        binding.rbPasienWanita.isChecked = true
                        binding.imageprofiledokter2.setImageResource(R.drawable.ic_woman);
                    }

                    binding.apply {
                        etEmail.setText(user!!.email)
                        etNama.setText(user!!.nama)
                        etBB.setText(user!!.beratbadan.toString())
                        etTB.setText(user!!.tinggibadan.toString())
                        etPasienTanggal4.setText(user!!.tanggallahir)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun getDateCalendar(){
        val cal : Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }

    override fun onDateSet(view: DatePicker?, year1: Int, month1: Int, dayOfMonth: Int) {
        savedday = dayOfMonth
        savedmonth = month1
        savedyear = year1
        val dob: Calendar = Calendar.getInstance()
        val today: Calendar = Calendar.getInstance()
        dob.set(savedyear,savedmonth,savedday)
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        val ageInt = age
        if (ageInt < 10) {
            Toast.makeText(this, "Usia harus minimal 10 tahun", Toast.LENGTH_SHORT).show()
        }
        else{
            binding.etPasienTanggal4.setText("$savedday-$savedmonth-$savedyear")
        }
    }

    private fun updatData(nama:String, tb:Int, bb:Int, tanggal:String,jk:String){
        val ref = FirebaseDatabase.getInstance().getReference("PASIEN").child(firebaseUser.uid)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var user =snapshot.getValue<Pasien>(Pasien::class.java)
                user!!.nama =  nama
                user!!.tinggibadan = tb
                user!!.beratbadan = bb
                user!!.tanggallahir = tanggal
                user!!.jeniskelamin = jk
                ref.setValue(user)
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
            overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
        }
    }
    private fun Loading() {
        binding.mainLayout.visibility = View.GONE
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        Handler().postDelayed({
            progressDialog.dismiss()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            binding.mainLayout.visibility = View.VISIBLE
        }, 2000)
    }
}