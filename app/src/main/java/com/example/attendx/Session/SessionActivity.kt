package com.example.attendx.Session

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendx.Adapters.StudentAdapter
import com.example.attendx.Models.Student
import com.example.attendx.R
import com.google.firebase.firestore.FirebaseFirestore

class SessionActivity : AppCompatActivity() {

    private lateinit var recyclerStudents: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val studentList = mutableListOf<Student>()
    private lateinit var adapter: StudentAdapter

    private lateinit var qrImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        recyclerStudents = findViewById(R.id.recyclerStudents)
        recyclerStudents.layoutManager = LinearLayoutManager(this)

        adapter = StudentAdapter(studentList)
        recyclerStudents.adapter = adapter

        val classCode = intent.getStringExtra("classCode") ?: ""

        loadStudents(classCode)
    }

    private fun loadStudents(classCode: String){

        db.collection("classes")
            .document(classCode)
            .collection("students")
            .addSnapshotListener { snapshot, _ ->

                snapshot?.let {

                    studentList.clear()

                    for(doc in it){
                        val student = doc.toObject(Student::class.java)
                        studentList.add(student)
                    }

                    adapter.notifyDataSetChanged()
                    //commit krdo
                }
            }
    }
}