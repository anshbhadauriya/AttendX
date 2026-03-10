package com.example.attendx.Login

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.attendx.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private var role: String = "student"

    // Common fields
    private lateinit var tilName: TextInputLayout
    private lateinit var tilUserId: TextInputLayout
    private lateinit var tilPhone: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var etUserId: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText

    // Teacher-only
    private lateinit var tvSubjectLabel: TextView
    private lateinit var tilSubject: TextInputLayout
    private lateinit var etSubject: TextInputEditText
    private lateinit var tvDeptLabel: TextView
    private lateinit var tilDepartment: TextInputLayout
    private lateinit var etDepartment: TextInputEditText

    // Student-only
    private lateinit var tvClassLabel: TextView
    private lateinit var tilClass: TextInputLayout
    private lateinit var etClass: TextInputEditText
    private lateinit var tvCourseLabel: TextView
    private lateinit var tilCourse: TextInputLayout
    private lateinit var etCourse: TextInputEditText

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db = FirebaseFirestore.getInstance()

        role = intent.getStringExtra("role") ?: "student"

        initViews()
        applyRoleUI()
        setupListeners()
    }

    private fun initViews() {
        tilName = findViewById(R.id.tilName)
        tilUserId = findViewById(R.id.tilUserId)
        tilPhone = findViewById(R.id.tilPhone)
        tilPassword = findViewById(R.id.tilPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)
        etName = findViewById(R.id.etName)
        etUserId = findViewById(R.id.etUserId)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        tvSubjectLabel = findViewById(R.id.tvSubjectLabel)
        tilSubject = findViewById(R.id.tilSubject)
        etSubject = findViewById(R.id.etSubject)
        tvDeptLabel = findViewById(R.id.tvDeptLabel)
        tilDepartment = findViewById(R.id.tilDepartment)
        etDepartment = findViewById(R.id.etDepartment)

        tvClassLabel = findViewById(R.id.tvClassLabel)
        tilClass = findViewById(R.id.tilClass)
        etClass = findViewById(R.id.etClass)
        tvCourseLabel = findViewById(R.id.tvCourseLabel)
        tilCourse = findViewById(R.id.tilCourse)
        etCourse = findViewById(R.id.etCourse)
    }

    private fun applyRoleUI() {
        val badge = findViewById<TextView>(R.id.tvRoleBadge)
        val idLabel = findViewById<TextView>(R.id.tvIdLabel)

        if (role == "teacher") {
            badge.text = "👨‍🏫 Teacher"
            idLabel.text = "Employee ID"
            tilUserId.hint = "Enter your employee ID"

            // Show teacher fields
            tvSubjectLabel.visibility = View.VISIBLE
            tilSubject.visibility = View.VISIBLE
            tvDeptLabel.visibility = View.VISIBLE
            tilDepartment.visibility = View.VISIBLE

        } else {
            badge.text = "🎓 Student"
            idLabel.text = "Roll Number"
            tilUserId.hint = "Enter your roll number"

            // Show student fields
            tvClassLabel.visibility = View.VISIBLE
            tilClass.visibility = View.VISIBLE
            tvCourseLabel.visibility = View.VISIBLE
            tilCourse.visibility = View.VISIBLE
        }
    }

    private fun setupListeners() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<TextView>(R.id.tvSignIn).setOnClickListener {
            // Go back to login
            finishAffinity()
            // or: startActivity(Intent(this, Role::class.java)); finish()
        }

        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            if (validateInputs()) performRegistration()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        fun check(til: TextInputLayout, et: TextInputEditText, msg: String): Boolean {
            return if (et.text.toString().trim().isEmpty()) {
                til.error = msg; isValid = false; false
            } else { til.error = null; true }
        }

        check(tilName, etName, "Full name is required")
        check(tilUserId, etUserId, "ID is required")
        check(tilPhone, etPhone, "Phone number is required")

        if (role == "teacher") {
            check(tilSubject, etSubject, "Subject is required")
            check(tilDepartment, etDepartment, "Department is required")
        } else {
            check(tilClass, etClass, "Class/Section is required")
            check(tilCourse, etCourse, "Course is required")
        }

        val password = etPassword.text.toString().trim()
        val confirm = etConfirmPassword.text.toString().trim()

        if (password.isEmpty()) { tilPassword.error = "Password is required"; isValid = false }
        else if (password.length < 6) { tilPassword.error = "Min. 6 characters"; isValid = false }
        else tilPassword.error = null

        if (confirm.isEmpty()) { tilConfirmPassword.error = "Please confirm password"; isValid = false }
        else if (confirm != password) { tilConfirmPassword.error = "Passwords do not match"; isValid = false }
        else tilConfirmPassword.error = null

        return isValid
    }

    private fun performRegistration() {

        val name = etName.text.toString().trim()
        val userId = etUserId.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString().trim()

        val userMap = HashMap<String, Any>()

        userMap["name"] = name
        userMap["userId"] = userId
        userMap["phone"] = phone
        userMap["password"] = password
        userMap["role"] = role

        if (role == "teacher") {
            userMap["subject"] = etSubject.text.toString().trim()
            userMap["department"] = etDepartment.text.toString().trim()
        } else {
            userMap["class"] = etClass.text.toString().trim()
            userMap["course"] = etCourse.text.toString().trim()
        }

//        if(role=="teacher") {


            db.collection("users")
                .document(userId)
                .set(userMap)
                .addOnSuccessListener {

                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_LONG).show()
                    finish()

                }
                .addOnFailureListener {

                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()

                }
//        }
//        else{
//
//            db.collection("students")
//                .document(userId)
//                .set(userMap)
//                .addOnSuccessListener {
//
//                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_LONG).show()
//                    finish()
//
//                }
//                .addOnFailureListener {
//
//                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
//
//                }
//
//        }
    }
}