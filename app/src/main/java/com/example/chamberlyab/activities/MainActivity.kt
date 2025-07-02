package com.example.chamberlyab.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.chamberlyab.R
import com.example.chamberlyab.fragments.FeedFragment
import com.example.chamberlyab.fragments.ArchiveFragment
import com.example.chamberlyab.fragments.AIFragment
import com.example.chamberlyab.fragments.ProfileFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    // Firebase authentication instance
    private lateinit var auth: FirebaseAuth

    // Google Sign-In client
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge rendering (modern UI design)
        enableEdgeToEdge()

        // Set the layout from XML
        setContentView(R.layout.activity_main)

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance()

        // If user is not logged in, redirect to SignInActivity
        if (auth.currentUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish() // Close MainActivity
            return
        }

        // Configure Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // OAuth token for Firebase
            .requestEmail() // Request user's email
            .build()

        // Build GoogleSignInClient with the options
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Load the default fragment (Archive) when activity starts
        loadFragment(AIFragment())

        // Set up bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> loadFragment(AIFragment())       // Load AI assistant
                R.id.nav_archive -> loadFragment(ArchiveFragment()) // Load Archive
                R.id.nav_feed -> loadFragment(FeedFragment())       // Load Feed
                R.id.nav_profile -> loadFragment(ProfileFragment()) // Load Profile
            }
            true
        }
    }

    // Utility function to load and display a fragment in the container
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Logs out the user from Firebase and Google, then redirects to SignInActivity
    fun logout() {
        auth.signOut() // Firebase sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish() // Close MainActivity
        }
    }
}
