package com.example.attendx.Login

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.attendx.R

class SelectRoleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_role)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<CardView>(R.id.cardTeacher).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("role", "teacher")
            startActivity(intent)
        }

        findViewById<CardView>(R.id.cardStudent).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("role", "student")
            startActivity(intent)
        }
    }
}