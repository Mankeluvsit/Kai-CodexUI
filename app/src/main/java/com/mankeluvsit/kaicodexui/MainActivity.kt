package com.mankeluvsit.kaicodexui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mankeluvsit.codexui.CodexUiScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var showCodexUi by rememberSaveable { mutableStateOf(false) }

            MaterialTheme {
                if (showCodexUi) {
                    CodexUiScreen(onBack = { showCodexUi = false })
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(text = stringResource(id = R.string.kai_title))
                            Button(onClick = { showCodexUi = true }) {
                                Text(text = stringResource(id = R.string.open_codex_ui))
                            }
                        }
                    }
                }
            }
        }
    }
}
