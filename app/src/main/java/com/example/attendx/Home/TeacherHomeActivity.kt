package com.example.attendx.Home

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.attendx.R
import android.widget.ImageView

class TeacherHomeActivity : AppCompatActivity() {

    private lateinit var StartClass: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_teacher_home)

        initialize()
        setupListeners()
    }

    private fun initialize() {
        StartClass = findViewById(R.id.StartClass)
    }

    private fun setupListeners() {
        StartClass.setOnClickListener {
            Toast.makeText(this, "Opening QR Scanner...", Toast.LENGTH_SHORT).show()
            val teacherName = intent.getStringExtra("teacher_name") ?: "John Doe"
            val subjectCode= intent.getStringExtra("subject_code") ?: "MATH01"
            val sessionId = System.currentTimeMillis().toString()

            val qrData = "$teacherName|$subjectCode|$sessionId"

            generateQrCode(qrData)

        }
    }

    private fun generateQrCode(data: String){

        try {

            val writer = com.google.zxing.MultiFormatWriter()

            val matrix = writer.encode(
                data,
                com.google.zxing.BarcodeFormat.QR_CODE,
                500,
                500
            )

            val encoder = com.journeyapps.barcodescanner.BarcodeEncoder()

            val bitmap = encoder.createBitmap(matrix)

            val qrImage = findViewById<ImageView>(R.id.qrImage)

            qrImage.setImageBitmap(bitmap)

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
}