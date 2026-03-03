package com.example.attendx.Home

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendx.R

data class Classroom(
    val subjectName: String,
    val professorName: String,
    val classCode: String,
    val attendancePercent: Int,
    val colorHex: String
)

class ClassroomAdapter(
    private val classrooms: List<Classroom>,
    private val onItemClick: (Classroom) -> Unit
) : RecyclerView.Adapter<ClassroomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSubject: TextView = view.findViewById(R.id.tvSubjectName)
        val tvProfessor: TextView = view.findViewById(R.id.tvTeacherName)
        val tvPercent: TextView = view.findViewById(R.id.tvAttendancePercent)
        val tvClassCode: TextView = view.findViewById(R.id.tvClassCode)
        val tvSubjectIcon: TextView = view.findViewById(R.id.tvSubjectIcon)
        val progressAttendance: ProgressBar = view.findViewById(R.id.progressAttendance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_classroom, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val classroom = classrooms[position]
        
        holder.tvSubject.text = classroom.subjectName
        holder.tvProfessor.text = classroom.professorName
        holder.tvClassCode.text = "Code: ${classroom.classCode}"
        holder.tvPercent.text = "${classroom.attendancePercent}%"
        holder.tvSubjectIcon.text = classroom.subjectName.take(1).uppercase()
        holder.progressAttendance.progress = classroom.attendancePercent

        // Set dynamic background color for the badge
        val color = try { Color.parseColor(classroom.colorHex) } catch (e: Exception) { Color.GRAY }
        val drawable = holder.tvSubjectIcon.background as? GradientDrawable
        drawable?.setColor(color) ?: run {
            holder.tvSubjectIcon.setBackgroundColor(color)
        }

        holder.itemView.setOnClickListener { onItemClick(classroom) }
    }

    override fun getItemCount() = classrooms.size
}
