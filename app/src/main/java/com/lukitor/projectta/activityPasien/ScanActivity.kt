package com.lukitor.projectta.activityPasien

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lukitor.projectta.Model.Dokter
import com.lukitor.projectta.Model.HistoryTransaksi
import com.lukitor.projectta.Model.Obat
import com.lukitor.projectta.Model.Penyakit
import com.lukitor.projectta.TempClass.Disease
import com.lukitor.projectta.TempClass.Drug
import com.lukitor.projectta.TempClass.UsedDrug
import com.lukitor.projectta.TempClass.User
import com.lukitor.projectta.databinding.ActivityScanBinding
import com.lukitor.projectta.ml.ModalpenyakitkulitV1
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.lang.Math.max
import java.lang.Math.min
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*


class ScanActivity : AppCompatActivity() {
    val imageSize = 32
    lateinit var binding: ActivityScanBinding
    var users = mutableListOf<User>() // Daftar pengguna
    var usedDrugs = mutableListOf<UsedDrug>() // Daftar obat yang digunakan
    var drugs = mutableListOf<Drug>() // Daftar obat
    var diseases = mutableListOf<Disease>() // Daftar penyakit
    lateinit var rekomendasiobat: MutableList<String>
    private lateinit var listPenyakit : MutableList<Penyakit>
    private lateinit var listobat : MutableList<Obat>
    var penyakit = ""
    var nama1 = ""
    var nama2 = ""
    var nama3= ""
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listPenyakit = mutableListOf()
        listobat = mutableListOf()
        rekomendasiobat = mutableListOf()
        //loadData()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi")
        builder.setMessage("Melakukan scan akan mengurangi jatah pemakaian, yakin ingin lanjut ?")
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
        }, 2000) // 5000 milliseconds = 5 seconds

        binding.imgGaleri.setOnClickListener{
            //pickImageFromGallery()
            builder.setPositiveButton("Ya") { dialog, which ->
                // Handle "Yes" button click
                // Perform the desired action here
                val cameraIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(cameraIntent, 1)
                Toast.makeText(this, "Jatah scan dikurangi 1", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("Tidak") { dialog, which ->
                // Handle "No" button click
                // Perform the desired action here
                Toast.makeText(this, "Scan dibatalkan", Toast.LENGTH_SHORT).show()
            }
            val dialog = builder.create()
            dialog.show()

        }
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.imgCamera.setOnClickListener{
            //pickImageFromCamera()
            //pickImageFromGallery()
            builder.setPositiveButton("Ya") { dialog, which ->
                // Handle "Yes" button click
                // Perform the desired action here
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, 3)
                    Toast.makeText(this, "Jatah scan dikurangi 1", Toast.LENGTH_SHORT).show()
                }
                else {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), 100);
                    Toast.makeText(this, "Jatah scan dikurangi 1", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Tidak") { dialog, which ->
                // Handle "No" button click
                // Perform the desired action here
                Toast.makeText(this, "Scan dibatalkan", Toast.LENGTH_SHORT).show()
            }
            val dialog = builder.create()
            dialog.show()

        }
    }
    fun classifyImage(image: Bitmap) {
        val model = ModalpenyakitkulitV1.newInstance(applicationContext)
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 32, 32, 3), DataType.FLOAT32)
        val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(imageSize * imageSize)
        image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
        var pixel = 0
        for (i in 0 until imageSize) {
            for (j in 0 until imageSize) {
                val value = intValues[pixel++]
                byteBuffer.putFloat((value shr 16 and 0xFF) * (1f / 1))
                byteBuffer.putFloat((value shr 8 and 0xFF) * (1f / 1))
                byteBuffer.putFloat((value and 0xFF) * (1f / 1))

            }
        }

        inputFeature0.loadBuffer(byteBuffer)
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val confidences = outputFeature0.floatArray
        var maxPos = 0
        var maxConfidence = 0f
        for (i in confidences.indices) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i]
                maxPos = i
            }
        }
        val classes = arrayOf("Biang", "Biduran", "Bisul", "Cacar", "Flek", "Jerawat", "Ketombe", "Kurap", "Panu", "Vitiligo")
        //dataPreparation(classes[maxPos])
        loadData(classes[maxPos])
        kNN()
        binding.txtPenyakitTerdeteksi.text = classes[maxPos]
        binding.layoutPenyakit.visibility = View.VISIBLE
        binding.layoutObat.visibility = View.VISIBLE
        model.close()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                var image = data?.extras!!["data"] as Bitmap?
                val dimension = min(image!!.width, image!!.height)
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
                binding.imgHolder.setImageBitmap(image)
                image = createScaledBitmap(image, imageSize, imageSize, false)
                classifyImage(image)
            } else {
                val dat = data?.data
                var image: Bitmap? = null
                try {
                    image = MediaStore.Images.Media.getBitmap(this.contentResolver, dat)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                binding.imgHolder.setImageBitmap(image)
                image = createScaledBitmap(image!!, imageSize, imageSize, false)
                classifyImage(image)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun loadData(namaPenyakit: String){
        var ctr = 0
        val ref1 = FirebaseDatabase.getInstance().getReference().child("PENYAKIT")
        ref1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    listPenyakit.clear()
                    for (h in snapshot.children){
                        val dokter = h.getValue(Penyakit::class.java)
                        if(dokter!=null){
                            listPenyakit.add(dokter)
                            diseases.add(Disease(dokter.id,dokter.namaPenyakit))
                            if (dokter.namaPenyakit == namaPenyakit){
                                val ref = FirebaseDatabase.getInstance().getReference("OBAT").child(dokter.id)
                                ref.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()){
                                            for (i in snapshot.children){
                                                val history = i.getValue(Obat::class.java)
                                                if(history!=null){
                                                    if (ctr == 0){
                                                        nama1 = history.namaobat
                                                    }
                                                    else if (ctr == 1){
                                                        nama2 = history.namaobat
                                                    }
                                                    else if (ctr == 2){
                                                        nama3 = history.namaobat
                                                    }
                                                    ctr++
                                                    listobat.add(history)
                                                    drugs.add(Drug(history.id,history.idpenyakit,history.namaobat))
                                                    rekomendasiobat.add(history.namaobat)
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
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun kNN(){
//        val random = Random()
//
//        val randomElements = mutableListOf<String>()
//
//        while (randomElements.size < 3) {
//            val randomIndex = random.nextInt(rekomendasiobat.size)
//            val randomElement = rekomendasiobat[randomIndex]
//            randomElements.add(randomElement)
//        }

        binding.txtNamaObat1.text = nama1
        binding.txtNamaObat2.text = nama2
        binding.txtNamaObat3.text = nama3

    }
//    private fun dataPreparation(penyakit: String){
//        // Tambahkan 20 pengguna
//        for (i in 1..20) {
//            val userId = "User_$i"
//            val userName = "User_$i"
//            val gender = if (i % 2 == 0) "Pria" else "Wanita"
//            val age = (20..50).random()
//            users.add(User(userId, userName, gender, age))
//        }
//
//
//        // Tambahkan 20 obat yang digunakan
//        for (i in 1..20) {
//            val userId = "User_$i"
//            val usedDrug = UsedDrug(
//                userId,
//                getRandomDrug("Penyakit_biang", drugs),
//                getRandomDrug("Penyakit_biduran", drugs),
//                getRandomDrug("Penyakit_bisul", drugs),
//                getRandomDrug("Penyakit_cacar", drugs),
//                getRandomDrug("Penyakit_flek", drugs),
//                getRandomDrug("Penyakit_jerawat", drugs),
//                getRandomDrug("Penyakit_ketombe", drugs),
//                getRandomDrug("Penyakit_kurap", drugs),
//                getRandomDrug("Penyakit_panu", drugs),
//                getRandomDrug("Penyakit_vitiligo", drugs)
//            )
//            usedDrugs.add(usedDrug)
//        }
//
//        val penyakitToField = mapOf(
//            "biang" to "drugPenyakitBiang",
//            "biduran" to "drugPenyakitBiduran",
//            "bisul" to "drugPenyakitBisul",
//            "cacar" to "drugPenyakitCacar",
//            "flek" to "drugPenyakitFlek",
//            "jerawat" to "drugPenyakitJerawat",
//            "ketombe" to "drugPenyakitKetombe",
//            "kurap" to "drugPenyakitKurap",
//            "panu" to "drugPenyakitPanu",
//            "vitiligo" to "drugPenyakitVitiligo"
//        )
//
//        val fieldpenyakit = penyakitToField[penyakit] // Field (atribut) penyakit yang sesuai
//        val newUser = User("UserBaru", "User Baru", "Pria", 25) // Pengguna baru
//        val used = UsedDrug("UserBaru","11","21","31","","","","","","","") // Obat yang digunakan oleh pengguna baru
//        var count = 0
//        val fields = listOf(
//            used.drugPenyakitBiang,
//            used.drugPenyakitBiduran,
//            used.drugPenyakitBisul,
//            used.drugPenyakitCacar,
//            used.drugPenyakitFlek,
//            used.drugPenyakitJerawat,
//            used.drugPenyakitKetombe,
//            used.drugPenyakitKurap,
//            used.drugPenyakitPanu,
//            used.drugPenyakitVitiligo
//        )
//
//        for (field in fields) {
//            if (!field.isNullOrEmpty()) {
//                count++
//            }
//        }
//
//    }

//    fun getRandomDrug(penyakit: String, drugs: List<Drug>): String? {
//        val drugsForDisease = drugs.filter { it.penyakit == penyakit }
//        return drugsForDisease.randomOrNull()?.id
//    }

}