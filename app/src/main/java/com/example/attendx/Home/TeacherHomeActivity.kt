package com.example.attendx.Home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.attendx.R
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.attendx.Session.SessionActivity
import com.google.firebase.firestore.FirebaseFirestore

class TeacherHomeActivity : AppCompatActivity() {

    private lateinit var StartClass: Button
    private lateinit var ViewClass: Button
    private lateinit var savecode: Button

    private lateinit var enterCode: EditText

    private lateinit var sessionAttendance: Button

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
        sessionAttendance=findViewById(R.id.sessionAttendance)

    }

    private fun setupListeners() {
        StartClass.setOnClickListener {
            val sharedPref = getSharedPreferences("AttendX", MODE_PRIVATE)
            val classCode = sharedPref.getString("class_code", "")

            if(classCode.isNullOrEmpty()){
                Toast.makeText(this,"Set class code first",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showSessionDialog(classCode)

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

        sessionAttendance.setOnClickListener {

            val sharedPref = getSharedPreferences("AttendX", MODE_PRIVATE)

            val classCode = sharedPref.getString("class_code", "")
            val sessionId = sharedPref.getString("current_session", "")

            if(sessionId.isNullOrEmpty()){
                Toast.makeText(this,"No session running",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, SessionActivity::class.java)
            intent.putExtra("classCode", classCode)
            intent.putExtra("sessionId", sessionId)

            startActivity(intent)
        }

        ViewClass.setOnClickListener {

            val sharedPref = getSharedPreferences("AttendX", MODE_PRIVATE)
            val classCode = sharedPref.getString("class_code", "")

            val intent = Intent(this, ViewClassActivity::class.java)
            intent.putExtra("class_code", classCode)

            startActivity(intent)
        }


    }

    private fun showSessionDialog(classCode: String) {

        val durations = arrayOf("5 minutes","10 minutes","15 minutes")

        AlertDialog.Builder(this)
            .setTitle("Start Attendance Session")
            .setItems(durations) { _, which ->

                val durationMinutes = when(which){
                    0 -> 5
                    1 -> 10
                    else -> 15
                }

                startSession(classCode, durationMinutes)
            }
            .show()
    }
    private fun startSession(classCode: String, duration: Int){

        val sessionId = System.currentTimeMillis().toString()

        val startTime = System.currentTimeMillis()
        val endTime = startTime + duration * 60 * 1000

        val sessionData = hashMapOf(
            "startTime" to startTime,
            "endTime" to endTime,
            "duration" to duration
        )

        db.collection("classes")
            .document(classCode)
            .collection("sessions")
            .document(sessionId)
            .set(sessionData)

        val qrData = "$classCode|$sessionId"

        generateQrCode(qrData)

        val sharedPref = getSharedPreferences("AttendX", MODE_PRIVATE)
        sharedPref.edit()
            .putString("current_session", sessionId)
            .apply()

        //session screen ke lie

//        val intent = Intent(this, SessionActivity::class.java)
//        intent.putExtra("classCode", classCode)
//        intent.putExtra("sessionId", sessionId)
//        startActivity(intent)



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