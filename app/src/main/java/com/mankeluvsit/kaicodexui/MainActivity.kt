package com.mankeluvsit.kaicodexui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mankeluvsit.kaicodexui.ui.KaiApp
import com.mankeluvsit.kaicodexui.ui.dashboard.DashboardViewModel
import com.mankeluvsit.kaicodexui.ui.dashboard.DashboardViewModelFactory
import com.mankeluvsit.kaicodexui.ui.theme.KaiCodexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { Root() }
    }

    @Composable
    private fun Root() {
        KaiCodexTheme {
            val viewModel: DashboardViewModel = viewModel(factory = DashboardViewModelFactory(applicationContext))
            KaiApp(viewModel = viewModel)
        }
    }
}
