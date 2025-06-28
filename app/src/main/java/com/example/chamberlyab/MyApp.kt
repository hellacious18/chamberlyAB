package com.example.chamberlyab

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.BuildConfig
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        val appCheck = FirebaseAppCheck.getInstance()

        if (BuildConfig.DEBUG) {
            appCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())

            // ✅ Correct way to retrieve the App Check debug token
            appCheck.getAppCheckToken(false).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token
                    Log.d("AppCheck", "Debug token: $token")
                } else {
                    Log.e("AppCheck", "Error getting App Check token", task.exception)
                }
            }
        } else {
            appCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
        }
    }
}
