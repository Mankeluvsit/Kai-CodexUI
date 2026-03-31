package com.mankeluvsit.kaicodexui.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mankeluvsit.kaicodexui.data.ServiceType
import com.mankeluvsit.kaicodexui.ui.screens.InAppBrowserScreen
import com.mankeluvsit.kaicodexui.ui.screens.StatusDashboardScreen
import com.mankeluvsit.kaicodexui.ui.viewmodel.DashboardViewModel

@Composable
fun KaiCodexApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val viewModel: DashboardViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = modifier
    ) {
        composable("dashboard") {
            StatusDashboardScreen(
                viewModel = viewModel,
                onOpenBrowser = { serviceType ->
                    navController.navigate("browser/${serviceType.name}")
                }
            )
        }
        composable(
            route = "browser/{serviceType}",
            arguments = listOf(navArgument("serviceType") { type = NavType.StringType })
        ) { backStackEntry ->
            val service = ServiceType.valueOf(backStackEntry.arguments?.getString("serviceType") ?: ServiceType.CODEX_UI.name)
            val config = viewModel.uiState.value.configs.first { it.type == service }
            InAppBrowserScreen(
                title = config.displayName,
                url = config.browserUrl,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
