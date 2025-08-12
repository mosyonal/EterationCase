package com.emreonal.eterationcase.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.emreonal.eterationcase.ui.navigation.AppRoot
import com.emreonal.eterationcase.ui.theme.EterationCaseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EterationCaseTheme {
                AppRoot()
            }
        }
    }
}