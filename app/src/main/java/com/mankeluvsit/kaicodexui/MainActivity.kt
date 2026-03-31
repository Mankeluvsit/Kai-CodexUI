package com.mankeluvsit.kaicodexui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mankeluvsit.kaicodexui.data.SettingsStore
import com.mankeluvsit.kaicodexui.ui.navigation.KaiNavGraph
import com.mankeluvsit.kaicodexui.ui.theme.KaiTheme
import com.mankeluvsit.kaicodexui.viewmodel.DashboardViewModel
import com.mankeluvsit.kaicodexui.viewmodel.DashboardViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsStore = SettingsStore(applicationContext)

        setContent {
            KaiTheme {
                val vm: DashboardViewModel = viewModel(
                    factory = DashboardViewModelFactory(settingsStore)
                )
                KaiNavGraph(vm)
            }
        }
    }
}
