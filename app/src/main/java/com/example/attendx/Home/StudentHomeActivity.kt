package com.example.attendx.Home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendx.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.zxing.integration.android.IntentIntegrator
import java.util.Calendar

class StudentHomeActivity : AppCompatActivity() {
    private lateinit var joinClass : LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_home)

        initialize()
        setupGreeting()
        setupScanButton()
        setupBottomNav()
        setupJoinClassButton()

    }

    private fun initialize(){
        joinClass=findViewById(R.id.btnJoinClass)
    }



    private fun setupGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good Morning,"
            hour < 17 -> "Good Afternoon,"
            else -> "Good Evening,"
        }
        findViewById<TextView>(R.id.tvGreeting).text = greeting

        // TODO: Replace with real student data from intent/SharedPrefs/DB
        val studentName = intent.getStringExtra("student_name") ?: "John Doe"
        val rollNo = intent.getStringExtra("roll_no") ?: "21CS042"
        findViewById<TextView>(R.id.tvStudentName).text = "$studentName 👋"
        findViewById<TextView>(R.id.tvRollNo).text = "Roll No: $rollNo"
        findViewById<TextView>(R.id.ivAvatar).text = studentName.first().uppercase()
    }



    private fun setupScanButton() {

        findViewById<LinearLayout>(R.id.btnScanQR).setOnClickListener {

            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scan Attendance QR")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(true)
            integrator.setOrientationLocked(true)

            integrator.initiateScan()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {

            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {

                val scannedData = result.contents

                Toast.makeText(this, "Scanned: $scannedData", Toast.LENGTH_LONG).show()

                processQRCode(scannedData)
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    private fun processQRCode(data: String) {

        val parts = data.split("|")

        if (parts.size == 3) {

            val teacher = parts[0]
            val subject = parts[1]
            val sessionId = parts[2]

            Toast.makeText(
                this,
                "Teacher: $teacher\nSubject: $subject\nSession: $sessionId",
                Toast.LENGTH_LONG
            ).show()

            // TODO: send to Firebase to mark attendance
        }
    }

    private fun setupJoinClassButton() {
        joinClass.setOnClickListener {
            Toast.makeText(this, "Opening ...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, JoinClassActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setupBottomNav() {
        findViewById<BottomNavigationView>(R.id.bottomNav)
            .setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> true
                    R.id.nav_classrooms -> {
                        Toast.makeText(this, "All Classrooms", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.nav_scan -> {
                        Toast.makeText(this, "Opening Scanner...", Toast.LENGTH_SHORT).show()
                        // TODO: startActivity(Intent(this, QRScannerActivity::class.java))
                        true
                    }
                    R.id.nav_profile -> {
                        Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
    }
}