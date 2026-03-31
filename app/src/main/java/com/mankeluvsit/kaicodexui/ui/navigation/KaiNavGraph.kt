package com.mankeluvsit.kaicodexui.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mankeluvsit.kaicodexui.network.ServiceClients
import com.mankeluvsit.kaicodexui.ui.screens.DashboardScreen
import com.mankeluvsit.kaicodexui.ui.screens.ServiceWebScreen
import com.mankeluvsit.kaicodexui.viewmodel.DashboardViewModel

@Composable
fun KaiNavGraph(viewModel: DashboardViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onOpenCodex = { navController.navigate("codex_web") },
                onOpenClaw = { navController.navigate("openclaw_web") }
            )
        }
        composable("codex_web") {
            ServiceWebScreen(url = ServiceClients.url(viewModel.uiState.value.codexConfig))
        }
        composable("openclaw_web") {
            ServiceWebScreen(url = ServiceClients.url(viewModel.uiState.value.openClawConfig))
        }
    }
}
