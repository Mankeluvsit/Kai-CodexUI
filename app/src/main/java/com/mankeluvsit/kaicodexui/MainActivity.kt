package com.mankeluvsit.kaicodexui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mankeluvsit.kaicodexui.ui.navigation.KaiCodexApp
import com.mankeluvsit.kaicodexui.ui.theme.KaiCodexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KaiCodexTheme {
                KaiCodexApp()
            }
        }
    }
}
