package com.vgtech.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vgtech.mobile.navigation.NavGraph
import com.vgtech.mobile.ui.theme.VGTechTheme

/**
 * MainActivity — Single Activity entry point for VG Tech Mobile.
 * The entire app is Jetpack Compose, routed via NavGraph.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VGTechTheme {
                NavGraph()
            }
        }
    }
}
