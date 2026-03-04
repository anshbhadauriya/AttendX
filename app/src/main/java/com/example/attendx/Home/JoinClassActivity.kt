package com.example.attendx.Home

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.attendx.R

class JoinClassActivity : AppCompatActivity() {
    private lateinit var class_code : EditText
    private lateinit var join_button : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_join_class)

        initialize()

        joinClass()



    }

    private fun initialize(){
        class_code=findViewById(R.id.class_code)
         join_button=findViewById(R.id.join_button)
    }
    private fun joinClass() {
        join_button.setOnClickListener {
            val code = class_code.text.toString()
            //TODO kal krna haiii
        }
    }


}