package com.example.attendx.Login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.attendx.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
                                                                           //conntected to activity_role
class Role : AppCompatActivity() {

    private lateinit var tilRollNumber: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etRollNumber: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnSignIn: Button
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvCreateAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_role)



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
            if (validateInputs()) performSignIn()
        }

        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Please contact your administrator", Toast.LENGTH_SHORT).show()
        }

        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, SelectRoleActivity::class.java))
        }

        etRollNumber.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) tilRollNumber.error = null }
        etPassword.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) tilPassword.error = null }
    }

    private fun validateInputs(): Boolean {
        val rollNumber = etRollNumber.text.toString().trim()
        val password = etPassword.text.toString().trim()
        var isValid = true
//.error se red color me show hone lagege invalid
        if (rollNumber.isEmpty()) { tilRollNumber.error = "Roll number / User ID is required"; isValid = false }
        else tilRollNumber.error = null

        if (password.isEmpty()) { tilPassword.error = "Password is required"; isValid = false }
        else if (password.length < 6) { tilPassword.error = "Password must be at least 6 characters"; isValid = false }
        else tilPassword.error = null

        return isValid
    }

    private fun performSignIn() {
        val rollNumber = etRollNumber.text.toString().trim()
        btnSignIn.isEnabled = false
        btnSignIn.text = "Signing In..."

        // TODO: Replace with real auth
        btnSignIn.postDelayed({
            btnSignIn.isEnabled = true
            btnSignIn.text = "Sign In"
            Toast.makeText(this, "Welcome, $rollNumber!", Toast.LENGTH_SHORT).show()
        }, 1500)
    }
}