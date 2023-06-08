package com.lukitor.projectta.activityDokter

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.lukitor.projectta.MainActivity
import com.lukitor.projectta.Model.Dokter
import com.lukitor.projectta.databinding.ActivityRegisterDokter2Binding
import java.io.ByteArrayOutputStream
import java.util.*


@Suppress("DEPRECATION")
class RegisterDokter2Activity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterDokter2Binding
    private lateinit var auth: FirebaseAuth
    lateinit var ref: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    lateinit var currentUserId: String
    var dataDokter1 = HashMap<String, String>()
    var dataDokter2 = HashMap<String, String>()
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001
    private val profilecode = 1101
    private val cvcode = 1102
    private val strcode = 1103
    private val sipcode = 1104
    var typcode = 0
    var profile: Uri? = null
    var cv: Uri? = null
    var str: Uri? = null
    var sip: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterDokter2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if(intent.hasExtra("datadokter2")){dataDokter2=intent.getSerializableExtra("datadokter2") as HashMap<String, String>}
        if(intent.hasExtra("datadokter1")){dataDokter1=intent.getSerializableExtra("datadokter1") as HashMap<String, String> }

        if(intent.hasExtra("datadokter2")){
            dataDokter2=intent.getSerializableExtra("datadokter2") as HashMap<String, String>
            binding.etTempatPraktek.setText(dataDokter2["tempatpraktek"])
            binding.etlamapraktek.setText(dataDokter2["lamapraktek"])
        }
        binding.tvCameraProfileDokter.setOnClickListener {
            typcode = profilecode
            pickImageFromGallery()
        }
        binding.tvGaleryProfileDokter.setOnClickListener {
            typcode = profilecode
            pickImageFromCamera()
        }
        binding.tvCameraCV.setOnClickListener {
            typcode = cvcode
            pickImageFromGallery()
        }
        binding.tvGaleryCV.setOnClickListener {
            typcode = cvcode
            pickImageFromCamera()
        }
        binding.tvCameraSTR.setOnClickListener {
            typcode = strcode
            pickImageFromGallery()
        }
        binding.tvGalerySTR.setOnClickListener {
            typcode = strcode
            pickImageFromCamera()
        }
        binding.tvCameraSIP.setOnClickListener {
            typcode = sipcode
            pickImageFromGallery()
        }
        binding.tvGalerySIP.setOnClickListener {
            typcode = sipcode
            pickImageFromCamera()
        }

        binding.fabConfirm.setOnClickListener{
            val tempatpraktek = binding.etTempatPraktek.text.toString().trim()
            val lamapraktek = binding.etlamapraktek.text.toString().trim()
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
            if (binding.imgUploadFotoDokter.drawable == null){
                binding.textView15.requestFocus()
                binding.textView15.error = "Foto Profile harus diisi"
                return@setOnClickListener
            }
            if (binding.imgUploadCV.drawable == null){
                binding.textView17.requestFocus()
                binding.textView17.error = "Foto CV harus diisi"
                return@setOnClickListener
            }
            if (binding.imgUploadSTR.drawable == null){
                binding.textView18.requestFocus()
                binding.textView18.error = "Foto STR harus diisi"
                return@setOnClickListener
            }
            if (binding.imgUploadSIP.drawable == null){
                binding.textView19.requestFocus()
                binding.textView19.error = "Foto SIP harus diisi"
                return@setOnClickListener
            }
            dataDokter1=intent.getSerializableExtra("datadokter1") as HashMap<String, String>
            registerUser(dataDokter1["email"].toString(),dataDokter1["nama"].toString(),dataDokter1["telp"].toString(),dataDokter1["kelamin"].toString(),dataDokter1["namabank"].toString(),dataDokter1["norek"].toString(),dataDokter1["an"].toString(), dataDokter1["tarif"].toString().toInt(), binding.etTempatPraktek.text.toString().trim(), binding.etlamapraktek.text.toString().trim().toInt(), "Dokter",1,dataDokter1["password"].toString())
        }

        binding.fabBack2.setOnClickListener {
            val tempatpraktek = binding.etTempatPraktek.text.toString().trim()
            val lamapraktek = binding.etlamapraktek.text.toString().trim()
            var intent = Intent(this,RegisterDokterActivity::class.java)
            dataDokter2["tempatpraktek"] = tempatpraktek
            dataDokter2["lamapraktek"] = lamapraktek
            intent.putExtra("datadokter1",dataDokter1)
            intent.putExtra("datadokter2",dataDokter2)
            startActivity(intent)
        }
    }

    // DATABASE SECTION
    private fun registerUser(email:String, nama: String, telp: String, jeniskelamin : String, namabank: String, norek: String, atasnama: String, tarif: Int,tempatpraktek: String, lamapraktek: Int, role: String, statusakun: Int, password: String) {
        val progressDialog = ProgressDialog(this@RegisterDokter2Activity)
        progressDialog.setTitle("Pendaftaran Dokter")
        progressDialog.setMessage("Sedang di Proses, Harap Tunggu.")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                currentUserId = auth.currentUser!!.uid
                uploadImageToFirebaseStorage()
                saveUser(email,nama,telp,jeniskelamin,namabank,norek,atasnama,tarif, tempatpraktek,lamapraktek,role,statusakun,progressDialog)
                auth.signOut()
            } else {
                Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun uploadImageToFirebaseStorage() {
        val ref1 = FirebaseStorage.getInstance().getReference("/images/$currentUserId/fotoprofile")
        val ref2 = FirebaseStorage.getInstance().getReference("/images/$currentUserId/fotocv")
        val ref3 = FirebaseStorage.getInstance().getReference("/images/$currentUserId/fotostr")
        val ref4 = FirebaseStorage.getInstance().getReference("/images/$currentUserId/fotosip")
        ref1.putFile(profile!!)
        ref2.putFile(cv!!)
        ref3.putFile(str!!)
        ref4.putFile(sip!!)
    }

    private fun saveUser(email:String, nama: String, telp: String, jeniskelamin : String, namabank: String, norek: String, atasnama: String, tarif: Int, tempatpraktek: String, lamapraktek: Int, role: String, statusakun: Int,progressDialog: ProgressDialog) {
        firebaseUser = auth.currentUser!!
        ref = FirebaseDatabase.getInstance().getReference("DOKTER")
        val data = Dokter(currentUserId,email,nama,telp,jeniskelamin,namabank,norek,atasnama,tarif,tempatpraktek,lamapraktek,role,statusakun,0)
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
                    Intent(this, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                }
                else{
                    progressDialog.dismiss()
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    // PICK IMAGE FROM CAMERA & GALLERY
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
            val path: String = MediaStore.Images.Media.insertImage(
                this.getContentResolver(),bmp,"Title",null
            )
            if (typcode == profilecode){
                profile = Uri.parse(path)
                binding.imgUploadFotoDokter.setImageBitmap(imgBitmap)
                checktypecode()
            }
            else if(typcode == cvcode){
                cv = Uri.parse(path)
                binding.imgUploadCV.setImageBitmap(imgBitmap)
                checktypecode()
            }
            else if(typcode == strcode){
                str = Uri.parse(path)
                binding.imgUploadSTR.setImageBitmap(imgBitmap)
                checktypecode()
            }
            else if(typcode == sipcode){
                sip = Uri.parse(path)
                binding.imgUploadSIP.setImageBitmap(imgBitmap)
                checktypecode()
            }
        }
        else if (resultCode == Activity.RESULT_OK && requestCode ==  IMAGE_PICK_CODE) {
            val uri = data?.getData()
            if (typcode == profilecode){
                profile = data?.data
                binding.imgUploadFotoDokter.setImageURI(uri)
                checktypecode()
            }
            else if(typcode == cvcode){
                cv = data?.data
                binding.imgUploadCV.setImageURI(uri)
                checktypecode()
            }
            else if(typcode == strcode){
                str = data?.data
                binding.imgUploadSTR.setImageURI(uri)
                checktypecode()
            }
            else if(typcode == sipcode){
                sip = data?.data
                binding.imgUploadSIP.setImageURI(uri)
                checktypecode()
            }
        }
    }
    fun checktypecode() {
        if (typcode == profilecode) {
            val imagePopup = ImagePopup(this)
            imagePopup.initiatePopup(binding.imgUploadFotoDokter.drawable) // Load Image from Drawable
            binding.imgUploadFotoDokter.setOnClickListener {
                imagePopup.viewPopup();
            }
            typcode = 0
            binding.imgUploadFotoDokter.visibility = View.VISIBLE
        }
        else if (typcode == cvcode) {
            val imagePopupcv = ImagePopup(this)
            imagePopupcv.initiatePopup(binding.imgUploadCV.drawable) // Load Image from Drawable
            binding.imgUploadCV.setOnClickListener {
                imagePopupcv.viewPopup();
            }
            typcode = 0
            binding.imgUploadCV.visibility = View.VISIBLE
        }
        else if (typcode == strcode) {
            val imagePopupstr = ImagePopup(this)
            imagePopupstr.initiatePopup(binding.imgUploadSTR.drawable) // Load Image from Drawable
            binding.imgUploadSTR.setOnClickListener {
                imagePopupstr.viewPopup();
            }
            typcode = 0
            binding.imgUploadSTR.visibility = View.VISIBLE
        }
        else if (typcode == sipcode) {
            val imagePopupsip = ImagePopup(this)
            imagePopupsip.initiatePopup(binding.imgUploadSIP.drawable) // Load Image from Drawable
            binding.imgUploadSIP.setOnClickListener {
                imagePopupsip.viewPopup();
            }
            typcode = 0
            binding.imgUploadSIP.visibility = View.VISIBLE
        }
    }
}