package com.example.attendx.Login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.attendx.Home.StudentHomeActivity
import com.example.attendx.Home.TeacherHomeActivity
import com.example.attendx.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore

// connected to activity_role class
class Role : AppCompatActivity() {

    private lateinit var tilRollNumber: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etRollNumber: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnSignIn: Button
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvCreateAccount: TextView

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.authentication)

        db = FirebaseFirestore.getInstance()

        // AUTO LOGIN CHECK
        val sharedPref = getSharedPreferences("AttendXPrefs", MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)
        val role = sharedPref.getString("role", null)

        if (userId != null && role != null) {

            if (role == "teacher") {
                startActivity(Intent(this, TeacherHomeActivity::class.java))
            } else {
                startActivity(Intent(this, StudentHomeActivity::class.java))
            }

            finish()
            return
        }


        initViews()
        setupListeners()
    }

    private fun initViews() {
        tilRollNumber = findViewById(R.id.tilRollNumber)
        tilPassword = findViewById(R.id.tilPassword)
        etRollNumber = findViewById(R.id.etRollNumber)
        etPassword = findViewById(R.id.etPassword)
        btnSignIn = findViewById(R.id.btnSignIn)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvCreateAccount = findViewById(R.id.tvCreateAccount)
    }

    private fun setupListeners() {

        btnSignIn.setOnClickListener {
            if (validateInputs()) {
                performSignIn()
            }
        }

        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Please contact your administrator", Toast.LENGTH_SHORT).show()
        }

        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, SelectRoleActivity::class.java))
        }

        etRollNumber.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilRollNumber.error = null
        }

        etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilPassword.error = null
        }
    }

    private fun validateInputs(): Boolean {

        val rollNumber = etRollNumber.text.toString().trim()
        val password = etPassword.text.toString().trim()

        var isValid = true

        // .error se red color me show hone lagege invalid
        if (rollNumber.isEmpty()) {
            tilRollNumber.error = "Roll number / User ID is required"
            isValid = false
        } else {
            tilRollNumber.error = null
        }

        if (password.isEmpty()) {
            tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            tilPassword.error = null
        }

        return isValid
    }

    private fun performSignIn() {

        val userId = etRollNumber.text.toString().trim()
        val password = etPassword.text.toString().trim()

        btnSignIn.isEnabled = false
        btnSignIn.text = "Signing In..."

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val storedPassword = document.getString("password")
                    val role = document.getString("role")
                    val username = document.getString("name")

                    if (storedPassword == password) {

                        // Save session in SharedPreferences
                        val sharedPref = getSharedPreferences("AttendXPrefs", MODE_PRIVATE)

                        sharedPref.edit()
                            .putString("username", username)
                            .putString("userId", userId)
                            .putString("role", role)
                            .apply()

                        Toast.makeText(this, "Welcome $username", Toast.LENGTH_SHORT).show()

                        if (role == "teacher") {
                            startActivity(Intent(this, TeacherHomeActivity::class.java))
                        } else {
                            startActivity(Intent(this, StudentHomeActivity::class.java))
                        }

                        finish()

                    } else {
                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "User not registered", Toast.LENGTH_SHORT).show()
                }

                btnSignIn.isEnabled = true
                btnSignIn.text = "Sign In"

            }
            .addOnFailureListener {

                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()

                btnSignIn.isEnabled = true
                btnSignIn.text = "Sign In"
            }
    }
}