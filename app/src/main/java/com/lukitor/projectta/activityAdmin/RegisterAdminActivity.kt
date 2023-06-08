package com.lukitor.projectta.activityAdmin

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.lukitor.projectta.Model.Admin
import com.lukitor.projectta.R
import com.lukitor.projectta.databinding.ActivityRegisterAdminBinding

class RegisterAdminActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var ref: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    lateinit var binding: ActivityRegisterAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.imgBack.setOnClickListener {
            finish()
            overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPW.text.toString().trim()
            val konfpassword = binding.etKonfPw.text.toString().trim()
            val nama = binding.etNama.text.toString().trim()
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
            if (!Patterns.PHONE.matcher(telp).matches() || telp.length < 10) {
                binding.etTelp.error = "Nomor Telepon tidak valid"
                binding.etTelp.requestFocus()
                return@setOnClickListener
            }
            registerUser(email,password,nama,telp,kelamin,)
        }

    }

    private fun registerUser(email:String,password: String,nama: String,telp : String,jeniskelamin : String){
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Pendaftaran Pasien")
        progressDialog.setMessage("Sedang di Proses, Harap Tunggu.")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                saveUser(email,password,nama,telp,jeniskelamin,progressDialog)
            } else {
                Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        }
    }

    private fun saveUser(email:String,password: String,nama: String,telp : String,jeniskelamin : String,progressDialog: ProgressDialog) {
        val currentUserId = auth.currentUser!!.uid
        firebaseUser = auth.currentUser!!
        ref = FirebaseDatabase.getInstance().getReference("ADMIN")
        val data = Admin(currentUserId.toString(),email,nama,telp,jeniskelamin,"Admin")
        if (data!=null){
            ref.child(currentUserId.toString()).setValue(data).addOnCompleteListener {
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

}