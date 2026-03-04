package com.example.attendx.Home

import android.content.Intent
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_home)

        setupGreeting()
//        setupStats()
//        setupClassrooms()
        setupScanButton()
        setupBottomNav()
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

//    private fun setupStats() {
//        // TODO: Replace with real data from your DB/API
//        val present = 42
//        val absent = 8
//        val total = present + absent
//        val percent = if (total > 0) (present * 100) / total else 0
//
//        findViewById<TextView>(R.id.tvOverallPercent).text = "$percent%"
//        findViewById<TextView>(R.id.tvPresentCount).text = "$present"
//        findViewById<TextView>(R.id.tvAbsentCount).text = "$absent"
//        findViewById<TextView>(R.id.tvClassroomCount).text = "6 Classes"
//    }

//    private fun setupClassrooms() {
//        // Sample data — replace with real DB/API data
//        val classrooms = listOf(
//            Classroom("Mathematics",   "Prof. Sharma",  "MATH01", 88, "#4F46E5"),
//            Classroom("Physics",       "Dr. Mehta",     "PHY02",  72, "#7C3AED"),
//            Classroom("Data Structures","Ms. Gupta",    "DS03",   95, "#0891B2"),
//            Classroom("English",       "Mr. Khan",      "ENG04",  60, "#D97706"),
//            Classroom("Chemistry",     "Dr. Singh",     "CHEM05", 80, "#059669"),
//        )
//
//        findViewById<TextView>(R.id.tvClassroomCount).text = "${classrooms.size} Classes"
//
//        val rv = findViewById<RecyclerView>(R.id.rvClassrooms)
//        rv.layoutManager = LinearLayoutManager(this)
//        rv.adapter = ClassroomAdapter(classrooms) { classroom ->
//            Toast.makeText(this, "Opened: ${classroom.subjectName}", Toast.LENGTH_SHORT).show()
//            // TODO: val intent = Intent(this, ClassroomDetailActivity::class.java)
//            //       intent.putExtra("classCode", classroom.classCode)
//            //       startActivity(intent)
//        }
//    }

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