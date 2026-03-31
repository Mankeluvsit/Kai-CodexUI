package com.mankeluvsit.kaicodexui

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.DrawerValue

enum class TopLevelDestination {
    KAI_CHAT,
    CODEX_UI,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaiCodexUiApp() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedDestination by remember { mutableStateOf(TopLevelDestination.KAI_CHAT) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(text = stringResource(R.string.nav_chat)) },
                    selected = selectedDestination == TopLevelDestination.KAI_CHAT,
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    onClick = {
                        selectedDestination = TopLevelDestination.KAI_CHAT
                        scope.launch { drawerState.close() }
                    },
                )
                NavigationDrawerItem(
                    label = { Text(text = stringResource(R.string.nav_codex_ui)) },
                    selected = selectedDestination == TopLevelDestination.CODEX_UI,
                    icon = { Icon(Icons.Default.Code, contentDescription = null) },
                    onClick = {
                        selectedDestination = TopLevelDestination.CODEX_UI
                        scope.launch { drawerState.close() }
                    },
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (selectedDestination == TopLevelDestination.KAI_CHAT) {
                                stringResource(R.string.nav_chat)
                            } else {
                                stringResource(R.string.nav_codex_ui)
                            },
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.open_navigation_drawer),
                            )
                        }
                    },
                )
            },
        ) { paddingValues ->
            when (selectedDestination) {
                TopLevelDestination.KAI_CHAT -> KaiHomeScreen(paddingValues)
                TopLevelDestination.CODEX_UI -> CodexUiWebScreen(paddingValues)
            }
        }
    }
}

@Composable
private fun KaiHomeScreen(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.nav_chat),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = stringResource(R.string.codex_ui_description),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun CodexUiWebScreen(paddingValues: PaddingValues) {
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                loadUrl(context.getString(R.string.codex_ui_url))
            }
        },
    )
}
