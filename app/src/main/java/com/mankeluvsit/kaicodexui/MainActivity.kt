package com.mankeluvsit.kaicodexui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mankeluvsit.codexui.ui.CodexUiManagementPanel
import com.mankeluvsit.codexui.ui.CodexUiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                KaiCodexUiApp()
            }
        }
    }
}

private enum class Destination(val route: String, val label: String) {
    Dashboard("dashboard", "Kai Hub"),
    CodexManager("codex_manager", "CodexUI")
}

@Composable
private fun KaiCodexUiApp() {
    val navController = rememberNavController()
    val destinations = Destination.entries

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
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
            startDestination = Destination.Dashboard.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Destination.Dashboard.route) {
                Text(
                    text = "Kai is the primary hub. Use the CodexUI tab to manage server configuration, process state, and logs.",
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(Destination.CodexManager.route) {
                val vm: CodexUiViewModel = viewModel()
                CodexUiManagementPanel(controller = vm.controller, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
