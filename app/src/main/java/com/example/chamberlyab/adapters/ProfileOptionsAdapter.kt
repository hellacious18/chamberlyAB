package com.example.chamberlyab.adapters

// Import necessary Android components
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.chamberlyab.R
import com.example.chamberlyab.data.ProfileOption

// Adapter class for displaying a list of profile options in a ListView or Spinner
class ProfileOptionsAdapter(
    private val context: Context,                  // Context from calling Activity/Fragment
    private val options: List<ProfileOption>       // List of profile options to display
) : ArrayAdapter<ProfileOption>(context, 0, options) { // Call superclass with layoutResId = 0 (we'll handle it manually)

    // This method is called to create or reuse a view for each item in the list
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Reuse an existing view if available, otherwise inflate a new one
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_profile, parent, false)

        // Get references to the icon and text views within the layout
        val icon = view.findViewById<ImageView>(R.id.itemIcon)
        val text = view.findViewById<TextView>(R.id.itemText)

        // Get the current profile option based on position
        val option = options[position]

        // Set the icon and text for the current item
        icon.setImageResource(option.iconResId)
        text.text = option.title

        // Make "Delete Account" appear in red to emphasize its destructive nature
        if (option.title.equals("Delete Account")) {
            text.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
        } else {
            text.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }

        // Return the configured view to the ListView
        return view
    }
}
