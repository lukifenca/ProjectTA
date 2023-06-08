package com.lukitor.projectta.activityPasien

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.Model.Pasien
import com.lukitor.projectta.R
import com.lukitor.projectta.databinding.ActivityRegisterPasienBinding
import java.util.*

class RegisterPasienActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    lateinit var binding: ActivityRegisterPasienBinding
    private lateinit var auth: FirebaseAuth
    lateinit var ref: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser

    var day = 0
    var month = 0
    var year = 0
    var savedday = 0
    var savedmonth = 0
    var savedyear = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPasienBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnPasienDate.setOnClickListener {
            getDateCalendar()
            DatePickerDialog(this,this,year,month,day).show()
        }

        binding.imageView3.setOnClickListener {
            finish()
            overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
        }
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPW.text.toString().trim()
            val konfpassword = binding.etKonfPw.text.toString().trim()
            val nama = binding.etNama.text.toString().trim()
            var temptb = binding.etTB.text.toString().trim()
            var tempbb = binding.etBB.text.toString().trim()
            val date = binding.etPasienTanggal.text.toString().trim()
            val telp = binding.etTelp.text.toString().trim()
            var kelamin = ""
            if(binding.rbPasienPria.isChecked){
                kelamin = "Pria"
            }
            else if(binding.rbPasienWanita.isChecked){
                kelamin = "Wanita"
            }

            if (email.isEmpty()) {
                binding.etEmail.error = "Email harus disi"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Email tidak valid"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty() || password.length < 6) {
                binding.etPW.error = "Sandi harus di isi lebih dari 6 karakter"
                binding.etPW.requestFocus()
                return@setOnClickListener
            }
            if (password != konfpassword) {
                binding.etKonfPw.error = "Konfirmasi Sandi tidak sesuai"
                binding.etKonfPw.requestFocus()
                return@setOnClickListener
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
                binding.etPasienTanggal.error = "Tanggal Lahir Harus Di Isi"
                binding.etPasienTanggal.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.PHONE.matcher(telp).matches() || telp.length < 10) {
                binding.etTelp.error = "Nomor Telepon tidak valid"
                binding.etTelp.requestFocus()
                return@setOnClickListener
            }
            registerUser(email,password,nama,tb,bb, kelamin,date)
        }
    }

    private fun registerUser(email:String,password: String,nama: String,tinggibadan : Int,beratbadan : Int,jeniskelamin : String,tanggallahir: String){
        val progressDialog = ProgressDialog(this@RegisterPasienActivity)
        progressDialog.setTitle("Pendaftaran Pasien")
        progressDialog.setMessage("Sedang di Proses, Harap Tunggu.")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                saveUser(email,nama,tinggibadan,beratbadan,jeniskelamin,tanggallahir,progressDialog)
                auth.signOut()
            } else {
                progressDialog.dismiss()
                Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUser(email:String,nama: String,tinggibadan : Int,beratbadan : Int,jeniskelamin : String,tanggallahir: String,progressDialog: ProgressDialog) {
        val currentUserId = auth.currentUser!!.uid
        firebaseUser = auth.currentUser!!
        ref = FirebaseDatabase.getInstance().getReference("PASIEN")
        val data = Pasien(currentUserId,email,nama,tinggibadan,beratbadan,jeniskelamin,tanggallahir,"Pasien")
        if (data!=null){
            ref.child(currentUserId).setValue(data).addOnCompleteListener {
                if (it.isSuccessful) {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Pendaftaran Berhasil", Toast.LENGTH_SHORT).show()
                    firebaseUser?.sendEmailVerification().addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this, "Email Verifikasi telah dikirim", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            progressDialog.dismiss()
                            Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    auth.signOut()
                    finish()
                    overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
                }
                else{
                    progressDialog.dismiss()
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

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
            Toast.makeText(this, "Usia harus minimal 10 tahun untuk mendaftar", Toast.LENGTH_SHORT).show()
        }
        else{
            binding.etPasienTanggal.setText("$savedday-$savedmonth-$savedyear")
        }
    }
}