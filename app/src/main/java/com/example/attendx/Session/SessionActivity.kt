package com.example.attendx.Session

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        recyclerStudents = findViewById(R.id.recyclerStudents)
        recyclerStudents.layoutManager = LinearLayoutManager(this)

        adapter = StudentAdapter(studentList)
        recyclerStudents.adapter = adapter

        val classCode = intent.getStringExtra("classCode") ?: ""
        val sessionId = intent.getStringExtra("sessionId") ?: ""

        listenToAttendance(classCode, sessionId)
    }

    private fun listenToAttendance(classCode: String, sessionId: String){

        db.collection("classes")
            .document(classCode)
            .collection("sessions")
            .document(sessionId)
            .collection("attendees")
            .addSnapshotListener { snapshot, _ ->

                if(snapshot != null){

                    studentList.clear()

                    for(doc in snapshot){
                        val student = doc.toObject(Student::class.java)
                        studentList.add(student)
                    }

                    adapter.notifyDataSetChanged()
                }
            }
    }
}