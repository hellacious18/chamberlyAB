package com.example.chamberlyab.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.chamberlyab.bottomsheets.EditDreamBottomSheet
import com.example.chamberlyab.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ArchiveFragment : Fragment(R.layout.fragment_archive) {

    // UI components
    private lateinit var calendarView: CalendarView
    private lateinit var tvDreamTitle: TextView
    private lateinit var tvDreamContent: TextView
    private lateinit var ivOptionsMenu: ImageView

    // Firebase Firestore reference
    private val db = FirebaseFirestore.getInstance()

    // Currently selected date, defaults to today
    private var selectedDate = getTodayDate()

    // Get user's email or fallback to guest
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "guest@example.com"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Bind UI components
        calendarView = view.findViewById(R.id.calendarView)
        tvDreamTitle = view.findViewById(R.id.tvDreamTitle)
        tvDreamContent = view.findViewById(R.id.tvDreamContent)
        ivOptionsMenu = view.findViewById(R.id.ivOptionsMenu)

        // Prevent user from selecting future dates
        calendarView.maxDate = System.currentTimeMillis()

        // Load dream for today's date initially
        fetchDream(selectedDate)

        // Handle calendar date selection
        calendarView.setOnDateChangeListener { _, year, month, day ->
            val selected = "$day-${month + 1}-$year"
            val sdf = SimpleDateFormat("d-M-yyyy", Locale.getDefault())
            val selectedDateObj = sdf.parse(selected)
            val todayDateObj = sdf.parse(getTodayDate())

            if (selectedDateObj.after(todayDateObj)) {
                // Block future date selection
                tvDreamTitle.text = ""
                tvDreamContent.text = ""
                tvDreamTitle.hint = "Cannot log or view future dreams"
                tvDreamContent.hint = "Please select today or a past date"
                ivOptionsMenu.visibility = View.INVISIBLE
            } else {
                // Valid date — fetch and display dream
                selectedDate = selected
                fetchDream(selectedDate)
                ivOptionsMenu.visibility = View.VISIBLE
            }
        }

        // Tapping title/content allows editing
        tvDreamTitle.setOnClickListener {
            openEditBottomSheet()
        }

        tvDreamContent.setOnClickListener {
            openEditBottomSheet()
        }

        // Listen for updates from the edit bottom sheet
        parentFragmentManager.setFragmentResultListener("dream_updated", viewLifecycleOwner) { _, _ ->
            fetchDream(selectedDate) // Refresh dream content after update
        }

        // Show delete option menu
        ivOptionsMenu.setOnClickListener {
            showOptionsMenu(it)
        }
    }

    // Display popup menu with delete option
    private fun showOptionsMenu(anchor: View) {
        val popup = android.widget.PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.vertical_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_delete -> {
                    confirmDelete()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    // Launch the edit dream bottom sheet
    private fun openEditBottomSheet() {
        val editSheet = EditDreamBottomSheet(
            selectedDate,
            tvDreamTitle.text.toString(),
            tvDreamContent.text.toString()
        )
        editSheet.show(parentFragmentManager, "EditDreamSheet")
    }

    // Fetch dream from Firestore for a given date
    private fun fetchDream(date: String) {
        db.collection("userDreams")
            .document(userEmail)
            .collection("dreams")
            .document(date)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Populate UI with stored dream
                    tvDreamTitle.text = document.getString("title") ?: ""
                    tvDreamContent.text = document.getString("content") ?: ""
                    ivOptionsMenu.visibility = View.VISIBLE
                } else {
                    // No dream found — reset UI with hints
                    tvDreamTitle.text = ""
                    tvDreamContent.text = ""
                    tvDreamTitle.hint = "Title Of Dream"
                    tvDreamContent.hint = "Dream content goes here..."
                    ivOptionsMenu.visibility = View.INVISIBLE
                }
            }
    }

    // Show confirmation dialog and delete dream from Firestore
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
                        // Clear UI after deletion
                        tvDreamTitle.text = ""
                        tvDreamContent.text = ""
                        tvDreamTitle.hint = "Title Of Dream"
                        tvDreamContent.hint = "Dream content goes here..."
                        ivOptionsMenu.visibility = View.INVISIBLE
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Get today’s date in d-M-yyyy format (used as Firestore doc ID)
    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("d-M-yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}
