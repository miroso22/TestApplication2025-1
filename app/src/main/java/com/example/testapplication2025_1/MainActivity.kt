package com.example.testapplication2025_1

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.testapplication2025_1.notifications.NotificationsManager
import com.example.testapplication2025_1.ui.theme.TestApplication20251Theme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiKey = packageManager
            .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            .metaData
            .getString("com.google.android.geo.API_KEY")

        if (!Places.isInitialized() && apiKey != null) {
            Places.initialize(applicationContext, apiKey)
        }

        enableEdgeToEdge()
        setContent {
            TestApplication20251Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.getStringExtra(NotificationsManager.MESSAGE_KEY)?.let {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}