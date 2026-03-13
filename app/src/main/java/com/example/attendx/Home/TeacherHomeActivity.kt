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

    private lateinit var startClass: Button

    private lateinit var endSession: Button
    private lateinit var viewClass: Button
    private lateinit var savecode: Button
    private lateinit var enterCode: EditText
    private lateinit var sessionAttendance: Button

    private lateinit var qrImage: ImageView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_teacher_home)

        initialize()
        loadSavedCode()
        updateSessionButton()
        setupListeners()
    }

    private fun initialize() {
        startClass = findViewById(R.id.StartClass)
        endSession = findViewById(R.id.endSession)
        viewClass = findViewById(R.id.ViewClass)
        savecode = findViewById(R.id.savecode)
        enterCode = findViewById(R.id.enterCode)
        qrImage = findViewById(R.id.qrImage)
        sessionAttendance = findViewById(R.id.sessionAttendance)
    }

    private fun setupListeners() {
        startClass.setOnClickListener {

            val sharedPref = getSharedPreferences("AttendX", MODE_PRIVATE)
            val classCode = sharedPref.getString("class_code", "")
            val currentSession = sharedPref.getString("current_session", "")

            if (classCode.isNullOrEmpty()) {
                Toast.makeText(this, "Set class code first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!currentSession.isNullOrEmpty()) {
                Toast.makeText(this, "A session is already running", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showSessionDialog(classCode)
        }

        savecode.setOnClickListener {

            val newCode = enterCode.text.toString().trim()

            if (newCode.isEmpty()) {
                Toast.makeText(this, "Please enter a class code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences("AttendX", MODE_PRIVATE)
            val oldCode = sharedPref.getString("class_code", "")

            val sessionPref = getSharedPreferences("AttendXPrefs", MODE_PRIVATE)
            val teacherName = sessionPref.getString("username", "Unknown")

            if (!oldCode.isNullOrEmpty() && oldCode != newCode) {
                db.collection("classes")
                    .document(oldCode)
                    .delete()
            }

            val classData = hashMapOf(
                "classCode" to newCode,
                "teacherName" to teacherName
            )

            db.collection("classes")
                .document(newCode)
                .set(classData)
                .addOnSuccessListener {

                    sharedPref.edit()
                        .putString("class_code", newCode)
                        .apply()

                    Toast.makeText(this, "Class Code Updated", Toast.LENGTH_SHORT).show()
                }
        }

        sessionAttendance.setOnClickListener {
            val sharedPref = getSharedPreferences("AttendX", MODE_PRIVATE)
            val classCode = sharedPref.getString("class_code", "")
            val sessionId = sharedPref.getString("current_session", "")

            if (sessionId.isNullOrEmpty()) {
                Toast.makeText(this, "No session running", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, SessionActivity::class.java)
            intent.putExtra("classCode", classCode)
            intent.putExtra("sessionId", sessionId)
            startActivity(intent)
        }

        viewClass.setOnClickListener {
            val sharedPref = getSharedPreferences("AttendX", MODE_PRIVATE)
            val classCode = sharedPref.getString("class_code", "")

            if (classCode.isNullOrEmpty()) {
                Toast.makeText(this, "Set class code first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, ViewClassActivity::class.java)
            intent.putExtra("class_code", classCode)
            startActivity(intent)
        }

        endSession.setOnClickListener {

            val sharedPref = getSharedPreferences("AttendX", MODE_PRIVATE)

            sharedPref.edit()
                .remove("current_session")
                .apply()

            val qrImage = findViewById<ImageView>(R.id.qrImage)
            qrImage.setImageDrawable(null)

            Toast.makeText(this,"Session ended",Toast.LENGTH_SHORT).show()

            updateSessionButton()
        }
    }

    private fun showSessionDialog(classCode: String) {

        val durations = arrayOf("2 minutes", "3 minutes", "5 minutes", "Custom...")

        AlertDialog.Builder(this)
            .setTitle("Start Attendance Session")
            .setItems(durations) { _, which ->

                when(which){

                    0 -> startSession(classCode, 2)
                    1 -> startSession(classCode, 3)
                    2 -> startSession(classCode, 5)

                    3 -> {

                        val input = EditText(this)
                        input.hint = "Enter minutes"
                        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER

                        AlertDialog.Builder(this)
                            .setTitle("Custom Duration")
                            .setView(input)
                            .setPositiveButton("Start") { _, _ ->

                                val minutes = input.text.toString().toIntOrNull()

                                if(minutes == null || minutes <= 0){
                                    Toast.makeText(this,"Enter valid time",Toast.LENGTH_SHORT).show()
                                }else{
                                    startSession(classCode, minutes)
                                }

                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                }
            }
            .show()
    }

    private fun startSession(classCode: String, duration: Int) {

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
            .addOnSuccessListener {

                val qrData = "$classCode|$sessionId"
                generateQrCode(qrData)

                val sharedPref = getSharedPreferences("AttendX", MODE_PRIVATE)
                sharedPref.edit()
                    .putString("current_session", sessionId)
                    .apply()

                updateSessionButton()

                Toast.makeText(this, "Session started", Toast.LENGTH_SHORT).show()

                // AUTO END SESSION AFTER TIME
                android.os.Handler().postDelayed({

                    sharedPref.edit()
                        .remove("current_session")
                        .apply()

                    qrImage.setImageDrawable(null)

                    updateSessionButton()

                    Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show()

                }, duration * 60 * 1000L)

            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to start session", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateQrCode(data: String) {
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
    private fun updateSessionButton() {

        val sharedPref = getSharedPreferences("AttendX", MODE_PRIVATE)
        val sessionId = sharedPref.getString("current_session", "")

        if(sessionId.isNullOrEmpty()){
            endSession.visibility = Button.GONE
        }else{
            endSession.visibility = Button.VISIBLE
        }
    }
}