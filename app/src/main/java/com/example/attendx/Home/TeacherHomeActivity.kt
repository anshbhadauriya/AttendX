package com.example.attendx.Home

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.attendx.R
import android.widget.ImageView
import com.google.firebase.firestore.FirebaseFirestore

class TeacherHomeActivity : AppCompatActivity() {

    private lateinit var StartClass: Button
    private lateinit var ViewClass: Button
    private lateinit var savecode: Button

    private lateinit var enterCode: EditText
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_teacher_home)

        val data = hashMapOf(
            "project" to "AttendX"
        )

        db.collection("test")
            .document("demo")
            .set(data)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Firestore Connected!", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }

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

            val newCode = enterCode.text.toString()

            val sharedPref = getSharedPreferences("AttendX", MODE_PRIVATE)
            val oldCode = sharedPref.getString("class_code", "")

            // Delete old class if exists
            if (!oldCode.isNullOrEmpty() && oldCode != newCode) {
                db.collection("classes")
                    .document(oldCode)
                    .delete()
            }

            // Save new class
            val classData = hashMapOf(
                "classCode" to newCode,
                "teacherName" to "Ansh"
            )

            db.collection("classes")
                .document(newCode)
                .set(classData)

            // Save locally
            sharedPref.edit()
                .putString("class_code", newCode)
                .apply()

            Toast.makeText(this, "Class Code Updated", Toast.LENGTH_SHORT).show()
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