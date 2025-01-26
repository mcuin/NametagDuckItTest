package com.nametag.nametagduckittest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nametag.nametagduckittest.ui.theme.NametagDuckItTestTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the app to hols the composable app.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NametagDuckItTestTheme {
                NametagDuckItTestApp()
            }
        }
    }
}