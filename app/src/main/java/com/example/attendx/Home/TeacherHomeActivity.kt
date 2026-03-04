package com.example.attendx.Home

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.attendx.R
import android.widget.ImageView

class TeacherHomeActivity : AppCompatActivity() {

    private lateinit var StartClass: Button
    private lateinit var ViewClass: Button
    private lateinit var savecode: Button

    private lateinit var enterCode: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_teacher_home)

        initialize()
        loadSavedCode()
        setupListeners()
    }

    private fun initialize() {
        StartClass = findViewById(R.id.StartClass)
        ViewClass = findViewById(R.id.ViewClass)
        savecode = findViewById(R.id.savecode)
        enterCode = findViewById(R.id.enterCode)

    }

    private fun setupListeners() {
        StartClass.setOnClickListener {
            Toast.makeText(this, "Opening QR Scanner...", Toast.LENGTH_SHORT).show()
            val teacherName = intent.getStringExtra("teacher_name") ?: "John Doe"
            val subjectCode= intent.getStringExtra("subject_code") ?: "MATH01"
            val sessionId = System.currentTimeMillis().toString()  //unit time-> number of mili seconds passed since 1 Jan 1970

            val qrData = "$teacherName|$subjectCode|$sessionId"

            generateQrCode(qrData)

        }

        savecode.setOnClickListener {
//            Toast.makeText(this, "ShutUP...", Toast.LENGTH_SHORT).show()

            val code=enterCode.text.toString()
            val sharedPref = getSharedPreferences("AttendX", MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("class_code", code)      //saved to local memory
            editor.apply()

            Toast.makeText(this, "Class Code Saved", Toast.LENGTH_SHORT).show()
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

    private fun loadSavedCode() {

        val sharedPref = getSharedPreferences("AttendX", MODE_PRIVATE)

        val savedCode = sharedPref.getString("class_code", "")

        enterCode.setText(savedCode)

    }
}