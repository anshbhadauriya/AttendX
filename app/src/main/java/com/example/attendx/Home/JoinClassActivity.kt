package com.example.attendx.Home

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.attendx.R
import com.google.firebase.firestore.FirebaseFirestore

class JoinClassActivity : AppCompatActivity() {
    private lateinit var class_code : EditText
    private lateinit var join_button : Button

    private val db = FirebaseFirestore.getInstance()

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

            if(code.isEmpty()){
                Toast.makeText(this,"Enter class code",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            db.collection("classes")
                .document(code)
                .get()
                .addOnSuccessListener { document ->

                    if(document.exists()){

                        val studentData = hashMapOf(
                            "name" to "Ansh",
                            "joinedAt" to System.currentTimeMillis()
                        )
                        db.collection("classes")
                            .document(code)
                            .collection("students")
                            .add(studentData)

                        Toast.makeText(this,"Class Found! Joined Successfully",Toast.LENGTH_SHORT).show()

                    } else {

                        Toast.makeText(this,"Invalid Class Code",Toast.LENGTH_SHORT).show()

                    }

                }
                .addOnFailureListener {

                    Toast.makeText(this,"Error connecting to server",Toast.LENGTH_SHORT).show()

                }

        }
    }


}