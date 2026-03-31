package com.mankeluvsit.kaicodexui.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mankeluvsit.kaicodexui.ui.dashboard.DashboardScreen
import com.mankeluvsit.kaicodexui.ui.dashboard.DashboardViewModel

@Composable
fun KaiApp(viewModel: DashboardViewModel, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    Scaffold(modifier = modifier) { padding ->
        NavHost(navController = navController, startDestination = "dashboard") {
            composable("dashboard") {
                DashboardScreen(viewModel = viewModel, contentPadding = padding)
            }
        }
    }
}
