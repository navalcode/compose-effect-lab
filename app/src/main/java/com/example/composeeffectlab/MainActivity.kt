package com.example.composeeffectlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.composeeffectlab.ui.ComposeEffectLabApp
import com.example.composeeffectlab.ui.theme.ComposeEffectLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeEffectLabTheme {
                ComposeEffectLabApp()
            }
        }
    }
}

