package com.example.pays

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pays.ui.theme.CountryAppNavigation
import com.example.pays.ui.theme.PaysTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PaysTheme {
                CountryAppNavigation()
            }
        }
    }
}
