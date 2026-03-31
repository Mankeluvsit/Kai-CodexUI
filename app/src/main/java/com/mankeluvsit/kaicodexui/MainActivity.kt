package com.mankeluvsit.kaicodexui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mankeluvsit.kaicodexui.data.ConfigStore
import com.mankeluvsit.kaicodexui.data.DashboardRepository
import com.mankeluvsit.kaicodexui.network.ServiceGateway
import com.mankeluvsit.kaicodexui.ui.DashboardViewModel
import com.mankeluvsit.kaicodexui.ui.components.KaiScaffold
import com.mankeluvsit.kaicodexui.ui.screens.ServiceWebScreen
import com.mankeluvsit.kaicodexui.ui.screens.StatusDashboardScreen
import com.mankeluvsit.kaicodexui.ui.theme.KaiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = DashboardRepository(ConfigStore(applicationContext), ServiceGateway())
        setContent {
            KaiTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    KaiApp(repository)
                }
            }
        }
    }
}

@Composable
private fun KaiApp(repository: DashboardRepository) {
    val navController = rememberNavController()
    val vm: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory(repository))
    val uiState by vm.uiState.collectAsState()

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: "dashboard"
    KaiScaffold(currentRoute = currentRoute, onNavigate = { navController.navigate(it) }) {
        NavHost(navController = navController, startDestination = "dashboard") {
            composable("dashboard") {
                StatusDashboardScreen(
                    state = uiState,
                    onSaveCodex = vm::saveCodex,
                    onSaveClaw = vm::saveOpenClaw,
                    onStart = vm::start,
                    onStop = vm::stop
                )
            }
            composable("codexui") { ServiceWebScreen(url = uiState.codexUiConfig.endpoint()) }
            composable("openclaw") { ServiceWebScreen(url = uiState.openClawConfig.endpoint()) }
        }
    }
}
