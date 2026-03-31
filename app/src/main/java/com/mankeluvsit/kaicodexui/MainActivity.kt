package com.mankeluvsit.kaicodexui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mankeluvsit.codexui.ui.CodexServerManagementScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KaiCodexUiApp()
        }
    }
}

private data class Destination(val route: String, val label: String)

@Composable
fun KaiCodexUiApp() {
    val navController = rememberNavController()
    val destinations = listOf(
        Destination("kai_home", "Kai"),
        Destination("codex_server", "CodexUI")
    )

    MaterialTheme {
        Scaffold(
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                NavigationBar {
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
                            icon = { Text(destination.label.take(1)) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "kai_home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("kai_home") {
                    KaiHomeScreen(onOpenCodexUi = { navController.navigate("codex_server") })
                }
                composable("codex_server") {
                    CodexServerManagementScreen()
                }
            }
        }
    }
}

@Composable
private fun KaiHomeScreen(onOpenCodexUi: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Kai Integration Hub", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "Use Kai as the primary entrypoint to monitor and control the embedded CodexUI service."
        )
        Button(onClick = onOpenCodexUi) {
            Text("Open CodexUI Controls")
        }
    }
}
