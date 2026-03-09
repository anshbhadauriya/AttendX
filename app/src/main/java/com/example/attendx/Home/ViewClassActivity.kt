package com.example.attendx.Home
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.example.attendx.R
import android.widget.Toast
import com.example.attendx.Adapters.StudentAdapter
import com.example.attendx.Models.Student

class ViewClassActivity : AppCompatActivity() {

    private lateinit var recyclerStudents: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_class)

        recyclerStudents = findViewById(R.id.recyclerStudents)
        recyclerStudents.layoutManager = LinearLayoutManager(this)

        val classCode = intent.getStringExtra("class_code") ?: ""

        loadStudents(classCode)
    }

    private fun loadStudents(classCode: String){

        db.collection("classes")
            .document(classCode)
            .collection("students")
            .get()
            .addOnSuccessListener { result ->

                val studentList = mutableListOf<Student>()

                for(doc in result){
                    val student = doc.toObject(Student::class.java)
                    studentList.add(student)
                }

                recyclerStudents.adapter = StudentAdapter(studentList)
            }
            .addOnFailureListener {
                Toast.makeText(this,"Failed to load students",Toast.LENGTH_SHORT).show()
            }
    }
}