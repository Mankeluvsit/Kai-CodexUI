package com.mankeluvsit.kaicodexui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mankeluvsit.codexui.CodexUiControlScreen

private enum class TopLevelDestination(val route: String, val label: String) {
    Dashboard("dashboard", "Kai"),
    CodexUi("codexui", "CodexUI")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaiCodexUiApp() {
    val navController = rememberNavController()
    val destinations = TopLevelDestination.entries

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kai Integration Hub") })
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(destination.label) },
                        icon = {}
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TopLevelDestination.Dashboard.route) {
                DashboardScreen(onOpenCodex = { navController.navigate(TopLevelDestination.CodexUi.route) })
            }
            composable(TopLevelDestination.CodexUi.route) {
                CodexUiControlScreen()
            }
        }
    }
}
