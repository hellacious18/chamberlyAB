package com.example.chamberlyab.adapters

import android.content.Context
import android.graphics.Color.red
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.chamberlyab.R
import com.example.chamberlyab.data.ProfileOption

class ProfileOptionsAdapter(
    private val context: Context,
    private val options: List<ProfileOption>
) : ArrayAdapter<ProfileOption>(context, 0, options) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_profile, parent, false)

        val icon = view.findViewById<ImageView>(R.id.itemIcon)
        val text = view.findViewById<TextView>(R.id.itemText)

        val option = options[position]
        icon.setImageResource(option.iconResId)
        text.text = option.title

        // 🔴 Make "Delete Account" red
        if (option.title.equals("Delete Account")) {
            text.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
        } else {
            text.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }
        return view
    }
}
