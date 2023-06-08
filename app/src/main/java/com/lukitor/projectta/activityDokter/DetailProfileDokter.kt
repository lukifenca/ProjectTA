package com.lukitor.projectta.activityDokter

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.Model.Dokter
import com.lukitor.projectta.Model.Pasien
import com.lukitor.projectta.R
import com.lukitor.projectta.databinding.ActivityDetailProfileDokterBinding
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

@Suppress("DEPRECATION")
class DetailProfileDokter : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityDetailProfileDokterBinding
    private lateinit var firebaseUser: FirebaseUser
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001
    lateinit var Uri:Uri
    lateinit var Uri1:Uri
    lateinit var Uri2:Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailProfileDokterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            firebaseUser = auth.currentUser!!
            userInfo()

            val popup = PopupMenu(this, binding.Imageprofiledokter)
            popup.menu.add(Menu.NONE, 1, Menu.NONE, "Lihat Foto Profil")
            popup.menu.add(Menu.NONE, 2, Menu.NONE, "Ganti Foto Profile Melalui Camera")
            popup.menu.add(Menu.NONE, 3, Menu.NONE, "Ganti Foto Profile Melalui Galeri")
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> {// Load Image from Drawable
                        val imagePopupstr = ImagePopup(this)
                        imagePopupstr.initiatePopupWithPicasso(Uri)
                        imagePopupstr.viewPopup();

                        val imagePopupstr1 = ImagePopup(this)
                        imagePopupstr1.initiatePopupWithPicasso(Uri1)
                        binding.imgDetailDokterSTR.setOnClickListener {
                            imagePopupstr1.viewPopup();
                        }

                        val imagePopupstr2 = ImagePopup(this)
                        imagePopupstr2.initiatePopupWithPicasso(Uri2)
                        binding.imgDetailDokterSIP.setOnClickListener {
                            imagePopupstr2.viewPopup();
                        }
                        true
                    }
                    2 -> {
                        pickImageFromCamera()
                        true
                    }
                    3 -> {
                        pickImageFromGallery()
                        true
                    }
                    else -> false
                }

            }

            binding.imgDetailDokterSIP.setOnClickListener{

                val imagePopupstr2 = ImagePopup(this)
                imagePopupstr2.initiatePopupWithPicasso(Uri2)
                binding.imgDetailDokterSIP.setOnClickListener {
                    imagePopupstr2.viewPopup();
                }
            }
            binding.imgDetailDokterSTR.setOnClickListener {
                val imagePopupstr1 = ImagePopup(this)
                imagePopupstr1.initiatePopupWithPicasso(Uri1)
                binding.imgDetailDokterSTR.setOnClickListener {
                    imagePopupstr1.viewPopup();
                }
            }
            binding.Imageprofiledokter.setOnClickListener { it ->
                popup.show()
            }

            binding.btnBack.setOnClickListener{
                finish()
                overridePendingTransition(R.transition.nothing, R.transition.bottom_down)
            }

            binding.btnUpdate.setOnClickListener { it ->
                val nama = binding.etNama.text.toString().trim()
                val telp = binding.etTelp.text.toString().trim()
                val namabank = binding.etNamaBank.text.toString().trim()
                val norek = binding.etNoRek.text.toString().trim()
                val an = binding.etAN.text.toString().trim()
                val tarif = binding.etTarif.text.toString().trim()
                var kelamin = ""
                val tempatpraktek = binding.etTempatPraktek.text.toString().trim()
                val lamapraktek = binding.etlamapraktek.text.toString().trim()
                if(binding.rbDokterPria.isChecked){
                    kelamin = "Pria"
                }
                else if(binding.rbDokterWanita.isChecked){
                    kelamin = "Wanita"
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
                    binding.etTarif.error = "Tarif Konsultasi Harus Lebih Dari Rp.25000"
                    binding.etTarif.requestFocus()
                    return@setOnClickListener
                }
                if (tempatpraktek.isEmpty()) {
                    binding.etTempatPraktek.error = "Tempat Praktek harus disi"
                    binding.etTempatPraktek.requestFocus()
                    return@setOnClickListener
                }
                if (lamapraktek.isEmpty()) {
                    binding.etlamapraktek.error = "Lama Pengalaman Praktek harus diisi"
                    binding.etlamapraktek.requestFocus()
                    return@setOnClickListener
                }
                if (lamapraktek.toInt() < 1) {
                    binding.etlamapraktek.error = "Lama Pengalaman Praktek minimal harus 1 Tahun"
                    binding.etlamapraktek.requestFocus()
                    return@setOnClickListener
                }
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Memperbarui Data")
                progressDialog.setMessage("Sedang di Proses, Harap Tunggu.")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                updateData(nama,telp,kelamin,namabank,norek,an,tarif.toInt(),tempatpraktek,lamapraktek.toInt(),progressDialog)
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
        }
    }
    fun pickImageFromCamera() {
        // Intent to take a picture and return control to the calling application
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            applicationContext?.packageManager?.let{
                intent.resolveActivity(it).also {
                    startActivityForResult(intent, PERMISSION_CODE)
                }
            }
        }
    }
    fun pickImageFromGallery() {
        // Intent to pick image from gallery
        Intent(Intent.ACTION_PICK).also { intent ->
            applicationContext?.packageManager?.let{
                intent.type = "image/*"
                intent.resolveActivity(it).also {
                    startActivityForResult(intent, IMAGE_PICK_CODE)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PERMISSION_CODE) {
            val imgBitmap = data?.extras?.get("data") as Bitmap
            val baos =  ByteArrayOutputStream()
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            val bmp = data?.extras?.get("data") as Bitmap
            val bytes = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path: String = MediaStore.Images.Media.insertImage(this.getContentResolver(),bmp,"Title",null)
            Uri = android.net.Uri.parse(path)
            binding.Imageprofiledokter.setImageBitmap(imgBitmap)

        }
        else if (resultCode == Activity.RESULT_OK && requestCode ==  IMAGE_PICK_CODE) {
            val uri = data?.getData()
            Uri = data?.data!!
            binding.Imageprofiledokter.setImageURI(uri)
        }
    }
    private fun userInfo(){
        val ref = FirebaseDatabase.getInstance().getReference().child("DOKTER").child(auth.currentUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user =snapshot.getValue<Dokter>(Dokter::class.java)
                    if (user!!.jeniskelamin.toString() =="Pria"){
                        binding.rbDokterPria.isChecked = true
                        binding.rbDokterWanita.isChecked = false
                    }
                    else{
                        binding.rbDokterPria.isChecked = false
                        binding.rbDokterPria.isChecked = true
                    }
                    val uid = auth.currentUser!!.uid
                    val ref = Firebase.storage.reference.child("images/$uid/fotoprofile")
                    val ref1 = Firebase.storage.reference.child("images/$uid/fotostr")
                    val ref2 = Firebase.storage.reference.child("images/$uid/fotosip")
                    binding.apply {
                        etEmail.setText(user!!.email)
                        etNama.setText(user!!.nama)
                        etTelp.setText(user!!.telp.toString())
                        etNamaBank.setText(user!!.namabank.toString())
                        etNoRek.setText(user!!.norek.toString())
                        etTarif.setText(user!!.tarif.toString())
                        etAN.setText(user!!.atasnama.toString())
                        if (user!!.jeniskelamin.toString() =="Pria"){
                            binding.rbDokterPria.isChecked = true
                            binding.rbDokterWanita.isChecked = false
                        }
                        else{
                            binding.rbDokterPria.isChecked = false
                            binding.rbDokterWanita.isChecked = true
                        }
                        ref.downloadUrl
                            .addOnSuccessListener { uri ->
                                Picasso.get().load(uri).into(Imageprofiledokter)
                                Uri = uri
                            }
                            .addOnFailureListener { exception ->
                                Log.e("MainActivity", "Image loading failed", exception)
                        }
                        ref1.downloadUrl
                            .addOnSuccessListener { uri ->
                                Picasso.get().load(uri).into(imgDetailDokterSTR)
                                Uri1 = uri
                            }
                            .addOnFailureListener { exception ->
                                Log.e("MainActivity", "Image loading failed", exception)
                        }
                        ref2.downloadUrl
                            .addOnSuccessListener { uri ->
                                Picasso.get().load(uri).into(imgDetailDokterSIP)
                                Uri2 = uri
                            }
                            .addOnFailureListener { exception ->
                                Log.e("MainActivity", "Image loading failed", exception)
                        }
                        etTempatPraktek.setText(user!!.tempatpraktek.toString())
                        etlamapraktek.setText(user!!.lamapraktek.toString())
//
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun updateData(nama: String, telp: String, jeniskelamin : String, namabank: String, norek: String, atasnama: String, tarif: Int,tempatpraktek: String, lamapraktek: Int, progressDialog: ProgressDialog){
        val ref = FirebaseDatabase.getInstance().getReference("DOKTER").child(auth.currentUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var user =snapshot.getValue<Dokter>(Dokter::class.java)
                user!!.nama =  nama
                user!!.telp = telp
                user!!.jeniskelamin = jeniskelamin
                user!!.namabank = namabank
                user!!.norek = norek
                user!!.atasnama = atasnama
                user!!.tarif = tarif
                user!!.tempatpraktek = tempatpraktek
                user!!.lamapraktek = lamapraktek
                ref.setValue(user)
                progressDialog.dismiss()
                Toast.makeText(this@DetailProfileDokter, "Pembaruan data telah berhasil", Toast.LENGTH_SHORT).show()
            }
            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(this@DetailProfileDokter, "Pembaruan data gagal", Toast.LENGTH_SHORT).show()
                TODO("Not yet implemented")
            }
        })
        val currentUserId = auth.currentUser!!.uid
        val ref1 = FirebaseStorage.getInstance().getReference("/images/$currentUserId/fotoprofile")
        ref1.putFile(Uri)
    }
}