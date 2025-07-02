package com.example.chamberlyab.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.chamberlyab.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

// Activity that handles Google Sign-In
class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth // Firebase authentication instance
    private lateinit var googleSignInClient: GoogleSignInClient // Google Sign-In client
    private val RC_SIGN_IN = 9001 // Request code for sign-in intent

    override fun onStart() {
        super.onStart()

        // Enable App Check with debug provider (for development only)
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
        )

        // If user is already signed in, go to MainActivity
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enables edge-to-edge layout
        setContentView(R.layout.activity_sign_in) // Set layout for sign-in screen

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Request ID token
            .requestEmail() // Request user's email
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso) // Build client with options
        auth = FirebaseAuth.getInstance() // Initialize Firebase Auth

        // Set up click listener for Sign-In button
        findViewById<SignInButton>(R.id.signInButton).setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        // Launch Google Sign-In intent
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            // Handle result of sign-in intent
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!! // Get signed-in account
                firebaseAuthWithGoogle(account.idToken!!) // Authenticate with Firebase
            } catch (e: ApiException) {
                // Log sign-in failure
                Log.w("Google Sign-In", "signInResult:failed code=" + e.statusCode)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null) // Create Firebase credential
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()

                    // Navigate to MainActivity after successful sign-in
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // Log and show error message on failure
                    Log.w("Google Sign-In", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
