package com.lukitor.projectta.activityPasien

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.lukitor.projectta.databinding.ActivityScanKulitBinding
import com.lukitor.projectta.ml.ModalpenyakitkulitV1
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanKulitActivity : AppCompatActivity() {
    private lateinit var executorService: ExecutorService
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001
    lateinit var binding: ActivityScanKulitBinding
    private lateinit var bitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanKulitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvCameraSIP.setOnClickListener {
            pickImageFromGallery()
        }
        binding.tvGalerySIP.setOnClickListener {
            pickImageFromCamera()
        }

    }

    fun pickImageFromCamera() {
        // Intent to take a picture and return control to the calling application
        val also = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            applicationContext?.packageManager?.let {
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
            bitmap = imgBitmap
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            binding.imgUploadSIP2.setImageBitmap(imgBitmap)
            binding.imgUploadSIP2.visibility = View.VISIBLE
            //makePrediction(bitmap)
        }
        else if (resultCode == Activity.RESULT_OK && requestCode ==  IMAGE_PICK_CODE) {
            val uri = data?.data
            uri?.let {
                val inputStream = contentResolver.openInputStream(uri)
                val selectedPhoto = BitmapFactory.decodeStream(inputStream)
                bitmap = selectedPhoto
                binding.imgUploadSIP2.setImageURI(uri)
                binding.imgUploadSIP2.visibility = View.VISIBLE
                //makePrediction(bitmap)
            }
        }
    }

    private fun makePrediction(bitmap: Bitmap) {
        // Resize the image to match the input size of the model

        val model = ModalpenyakitkulitV1.newInstance(this)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true)
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 150, 150, 3), DataType.FLOAT32)
        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(resizedBitmap)
        val resizeOp = ResizeOp(150, 150, ResizeOp.ResizeMethod.BILINEAR)
        tensorImage = resizeOp.apply(tensorImage)
        val byteBuffer = tensorImage.buffer
        inputFeature0.loadBuffer(byteBuffer)

        // Run inference on the model
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Get the predicted class label from the output buffer
        val probabilities = outputFeature0.floatArray
        val predictedLabel = probabilities.indexOfFirst { it == probabilities.maxOrNull()!! }

        // Print the predicted class label
        println(predictedLabel)
        Toast.makeText(this,"asu",Toast.LENGTH_LONG).show()
        Toast.makeText(this,predictedLabel,Toast.LENGTH_LONG).show()
        model.close()
    }

    override fun onDestroy() {
        executorService.shutdownNow()
        super.onDestroy()
    }

}