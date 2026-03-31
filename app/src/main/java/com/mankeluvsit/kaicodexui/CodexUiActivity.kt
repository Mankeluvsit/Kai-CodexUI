package com.mankeluvsit.kaicodexui

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class CodexUiActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_codex_ui)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.codex_ui_title)

        val webView: WebView = findViewById(R.id.codex_webview)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        // CodexUI is a web frontend/CLI bridge; this hosts the public site by default.
        webView.loadUrl("https://codexui.com")
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
