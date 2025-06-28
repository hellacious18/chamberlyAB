package com.example.chamberlyab

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class ProfileFragment: Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logoutButton = view.findViewById<Button>(R.id.btnLogout)
        logoutButton.setOnClickListener {
            (activity as? MainActivity)?.logout()
        }
    }
}