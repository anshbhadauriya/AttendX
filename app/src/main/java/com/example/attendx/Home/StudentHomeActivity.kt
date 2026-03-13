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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import java.util.Calendar

data class JoinedClass(
    val classCode: String = "",
    val teacherName: String = ""
)

class StudentHomeActivity : AppCompatActivity() {

    private lateinit var joinClass: LinearLayout
    private lateinit var recyclerClasses: RecyclerView
//    private lateinit var tvWelcome: TextView
    private lateinit var tvStudentName: TextView
    private lateinit var tvRollNo: TextView
    private lateinit var ivAvatar: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_home)

        initialize()
        setupGreeting()
        setupScanButton()
        setupBottomNav()
        setupJoinClassButton()
        loadJoinedClasses()
    }

    private fun initialize() {

        joinClass = findViewById(R.id.btnJoinClass)
        recyclerClasses = findViewById(R.id.recyclerClasses)
//        tvWelcome = findViewById(R.id.tvWelcome)
        tvStudentName = findViewById(R.id.tvStudentName)
        tvRollNo = findViewById(R.id.tvRollNo)
        ivAvatar = findViewById(R.id.ivAvatar)

        recyclerClasses.layoutManager = LinearLayoutManager(this)
    }

    private fun setupGreeting() {

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val greeting = when {
            hour < 12 -> "Good Morning,"
            hour < 17 -> "Good Afternoon,"
            else -> "Good Evening,"
        }

        findViewById<TextView>(R.id.tvGreeting).text = greeting

        val sharedPref = getSharedPreferences("AttendXPrefs", MODE_PRIVATE)
        val username = sharedPref.getString("username", "User")
        val userId = sharedPref.getString("userId", "")

        tvStudentName.text = "Welcome $username"

        val studentName = username ?: "User"
        val rollNo = userId ?: ""

        tvStudentName.text = "$studentName 👋"
        tvRollNo.text = "Roll No: $rollNo"

        if (studentName.isNotEmpty()) {
            ivAvatar.text = studentName.first().uppercase()
        }
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

    private fun loadJoinedClasses() {

        val sharedPref = getSharedPreferences("AttendXPrefs", MODE_PRIVATE)
        val studentId = sharedPref.getString("userId", "") ?: ""

        db.collection("users")
            .document(studentId)
            .collection("joinedClasses")
            .get()
            .addOnSuccessListener { result ->

                val classList = mutableListOf<JoinedClass>()

                for (doc in result) {
                    val classObj = doc.toObject(JoinedClass::class.java)
                    classList.add(classObj)
                }

                recyclerClasses.adapter = ClassAdapter(classList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load classes", Toast.LENGTH_SHORT).show()
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

        if(parts.size != 2){
            Toast.makeText(this,"Invalid QR Code",Toast.LENGTH_SHORT).show()
            return
        }

        val classCode = parts[0]
        val sessionId = parts[1]

        val sharedPref = getSharedPreferences("AttendXPrefs", MODE_PRIVATE)

        val studentId = sharedPref.getString("userId","") ?: ""
        val studentName = sharedPref.getString("username","") ?: ""

        // Step 1: check if student joined class
        db.collection("classes")
            .document(classCode)
            .collection("students")
            .document(studentId)
            .get()
            .addOnSuccessListener { studentDoc ->

                if(!studentDoc.exists()){
                    Toast.makeText(this,"You are not enrolled in this class",Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Step 2: check session time
                db.collection("classes")
                    .document(classCode)
                    .collection("sessions")
                    .document(sessionId)
                    .get()
                    .addOnSuccessListener { sessionDoc ->

                        val endTime = sessionDoc.getLong("endTime") ?: 0

                        if(System.currentTimeMillis() > endTime){
                            Toast.makeText(this,"Session expired",Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }

                        // Step 3: mark attendance
                        val attendanceData = hashMapOf(
                            "name" to studentName,
                            "studentId" to studentId,
                            "joinedAt" to System.currentTimeMillis()
                        )

                        db.collection("classes")
                            .document(classCode)
                            .collection("sessions")
                            .document(sessionId)
                            .collection("attendees")
                            .document(studentId)
                            .set(attendanceData)

                        Toast.makeText(this,"Attendance marked successfully",Toast.LENGTH_SHORT).show()
                    }
            }
    }

    private fun setupJoinClassButton() {

        joinClass.setOnClickListener {

            Toast.makeText(this, "Opening Join Class...", Toast.LENGTH_SHORT).show()

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