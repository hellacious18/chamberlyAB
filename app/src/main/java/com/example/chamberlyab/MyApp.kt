package com.example.chamberlyab

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

// Custom Application class to initialize Firebase and configure global app settings
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase services for the app
        FirebaseApp.initializeApp(this)

        // Enable offline persistence for Firestore
        // This allows Firestore to cache data locally so users can access previously loaded data even without internet
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true) // Enables disk persistence
            .build()

        // Apply the Firestore settings to the instance
        FirebaseFirestore.getInstance().firestoreSettings = settings
    }
}
