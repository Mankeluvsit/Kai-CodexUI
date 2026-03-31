package com.mankeluvsit.kaicodexui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mankeluvsit.codexui.CodexUiManagementPanel
import com.mankeluvsit.codexui.CodexUiViewModel

private data class BottomDestination(val route: String, val label: String)

private val destinations = listOf(
    BottomDestination("kai_home", "Kai"),
    BottomDestination("codex_control", "CodexUI")
)

@Composable
fun KaiCodexUiApp() {
    val navController = rememberNavController()
    val codexViewModel: CodexUiViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(if (destination.route == "kai_home") "K" else "C") },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "kai_home",
            modifier = Modifier.padding(padding)
        ) {
            composable("kai_home") {
                KaiHomeScreen(
                    onManageCodexUi = { navController.navigate("codex_control") },
                    codexViewModel = codexViewModel
                )
            }
            composable("codex_control") {
                CodexUiManagementPanel(viewModel = codexViewModel)
            }
        }
    }
}
