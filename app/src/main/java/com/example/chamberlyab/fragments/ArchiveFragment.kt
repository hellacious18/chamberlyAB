package com.example.chamberlyab.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.chamberlyab.EditDreamBottomSheet
import com.example.chamberlyab.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ArchiveFragment: Fragment(R.layout.fragment_archive){
    private lateinit var calendarView: CalendarView
    private lateinit var tvDreamTitle: TextView
    private lateinit var tvDreamContent: TextView
    private lateinit var editDream: ImageView
    private lateinit var deleteDream: ImageView

    private val db = FirebaseFirestore.getInstance()
    private var selectedDate = getTodayDate()
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "guest@example.com"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        calendarView = view.findViewById(R.id.calendarView)
        tvDreamTitle = view.findViewById(R.id.tvDreamTitle)
        tvDreamContent = view.findViewById(R.id.tvDreamContent)
        editDream = view.findViewById(R.id.editDream)
        deleteDream = view.findViewById(R.id.deleteDream)

        // Prevent future date selection visually
        calendarView.maxDate = System.currentTimeMillis()

        fetchDream(selectedDate)

        calendarView.setOnDateChangeListener { _, year, month, day ->
            val selected = "$day-${month + 1}-$year"
            val sdf = SimpleDateFormat("d-M-yyyy", Locale.getDefault())
            val selectedDateObj = sdf.parse(selected)
            val todayDateObj = sdf.parse(getTodayDate())

            if (selectedDateObj.after(todayDateObj)) {
                tvDreamTitle.text = ""
                tvDreamContent.text = ""
                tvDreamTitle.hint = "Cannot log or view future dreams"
                tvDreamContent.hint = "Please select today or a past date"
                editDream.visibility = View.INVISIBLE
                deleteDream.visibility = View.INVISIBLE
            } else {
                selectedDate = selected
                fetchDream(selectedDate)
            }
        }

        editDream.setOnClickListener {
            val editSheet = EditDreamBottomSheet(
                selectedDate,
                tvDreamTitle.text.toString(),
                tvDreamContent.text.toString()
            )
            editSheet.show(parentFragmentManager, "EditDreamSheet")
        }

        parentFragmentManager.setFragmentResultListener("dream_updated", viewLifecycleOwner) { _, _ ->
            fetchDream(selectedDate) // Refresh dream content
        }

        deleteDream.setOnClickListener {
            confirmDelete()
        }
    }

    private fun fetchDream(date: String) {
        db.collection("userDreams")
            .document(userEmail)
            .collection("dreams")
            .document(date)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    tvDreamTitle.text = document.getString("title") ?: ""
                    tvDreamContent.text = document.getString("content") ?: ""
                    deleteDream.visibility = View.VISIBLE
                    editDream.visibility = View.VISIBLE
                } else {
                    tvDreamTitle.text = ""
                    tvDreamContent.text = ""
                    tvDreamTitle.hint = "Title Of Dream"
                    tvDreamContent.hint = "Dream content goes here..."
                    deleteDream.visibility = View.INVISIBLE
                }
            }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(requireContext())
            .setIcon(R.drawable.app_icon)
            .setTitle("Delete Dream")
            .setMessage("Are you sure you want to delete this dream?")
            .setPositiveButton("Yes") { _, _ ->
                db.collection("userDreams")
                    .document(userEmail)
                    .collection("dreams")
                    .document(selectedDate)
                    .delete()
                    .addOnSuccessListener {
                        tvDreamTitle.text = ""
                        tvDreamContent.text = ""
                        tvDreamTitle.hint = "Title Of Dream"
                        tvDreamContent.hint = "Dream content goes here..."
                        deleteDream.visibility = View.INVISIBLE
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("d-M-yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}