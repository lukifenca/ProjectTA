package com.lukitor.projectta.activityDokter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lukitor.projectta.databinding.ActivityRegisterDokter2Binding

class RegisterDokter2Activity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterDokter2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterDokter2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}