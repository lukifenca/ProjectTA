package com.lukitor.projectta.activityDokter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.lukitor.projectta.VerifActivity
import com.lukitor.projectta.databinding.ActivityRegisterDokterBinding

class RegisterDokterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterDokterBinding
    var dataDokter1 = HashMap<String, String>()
    var dataDokter2 = HashMap<String, String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterDokterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.hasExtra("datadokter2")){dataDokter2=intent.getSerializableExtra("datadokter2") as HashMap<String, String>}
        if(intent.hasExtra("datadokter1")){
            dataDokter1=intent.getSerializableExtra("datadokter1") as HashMap<String, String>
            binding.etEmail.setText(dataDokter1["email"])
            binding.etPW.setText(dataDokter1["password"])
            binding.etKonfPw.setText(dataDokter1["konfpassword"])
            binding.etNama.setText(dataDokter1["nama"])
            binding.etTelp.setText(dataDokter1["telp"])
            binding.etNamaBank.setText(dataDokter1["namabank"])
            binding.etNoRek.setText(dataDokter1["norek"])
            binding.etTarif.setText(dataDokter1["tarif"])
            binding.etAN.setText(dataDokter1["an"])
            if (dataDokter1["kelamin"] == "Pria"){
                binding.rbDokterPria.isChecked = true
            }
            else{
                binding.rbDokterWanita.isChecked = true
            }
        }
        binding.fabNext.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPW.text.toString().trim()
            val konfpassword = binding.etKonfPw.text.toString().trim()
            val nama = binding.etNama.text.toString().trim()
            val telp = binding.etTelp.text.toString().trim()
            val namabank = binding.etNamaBank.text.toString().trim()
            val norek = binding.etNoRek.text.toString().trim()
            val an = binding.etAN.text.toString().trim()
            val tarif = binding.etTarif.text.toString().trim()
            var kelamin = ""
            if(binding.rbDokterPria.isChecked){
                kelamin = binding.rbDokterWanita.toString().trim()
            }
            else if(binding.rbDokterWanita.isChecked){
                kelamin = binding.rbDokterWanita.toString().trim()
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
            if (telp.isEmpty()) {
                binding.etTelp.error = "Nomor Telepon Harus Di Isi"
                binding.etTelp.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.PHONE.matcher(telp).matches() || telp.length < 10) {
                binding.etTelp.error = "Nomor Telepon tidak valid"
                binding.etTelp.requestFocus()
                return@setOnClickListener
            }
            if (namabank.isEmpty()) {
                binding.etNamaBank.error = "Nama Bank Harus Di Isi"
                binding.etNamaBank.requestFocus()
                return@setOnClickListener
            }
            if (norek.isEmpty()) {
                binding.etNoRek.error = "Nomer Rekening Harus Di Isi"
                binding.etNoRek.requestFocus()
                return@setOnClickListener
            }
            if (norek.length < 10) {
                binding.etNoRek.error = "Panjang Nomer Rekening Harus Lebih Dari 10 Angka"
                binding.etNoRek.requestFocus()
                return@setOnClickListener
            }
            if (an.isEmpty()) {
                binding.etAN.error = "Nama Pemilik Rekening Harus Di Isi"
                binding.etAN.requestFocus()
                return@setOnClickListener
            }
            if (tarif.isEmpty()) {
                binding.etTarif.error = "Tarif Konsultas Harus Di Isi"
                binding.etTarif.requestFocus()
                return@setOnClickListener
            }
            if (tarif.toInt() < 25000) {
                binding.etAN.error = "Tarif Konsultasi Harus Lebih Dari Rp.25000"
                binding.etAN.requestFocus()
                return@setOnClickListener
            }

            dataDokter1["email"] = email
            dataDokter1["password"] = password
            dataDokter1["konfpassword"] = konfpassword
            dataDokter1["nama"] = nama
            dataDokter1["telp"] = telp
            dataDokter1["kelamin"] = kelamin
            var intent = Intent(this,RegisterDokter2Activity::class.java)
            intent.putExtra("datadokter1",dataDokter1)
            intent.putExtra("datadokter2",dataDokter2)

            startActivity(intent)
        }
        binding.fabBack.setOnClickListener { finish() }
    }
}