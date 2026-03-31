package com.mankeluvsit.kaicodexui

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import com.mankeluvsit.kaicodexui.databinding.ActivityCodexUiBinding

class CodexUiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCodexUiBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodexUiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.codexToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.codexWebView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            webChromeClient = WebChromeClient()
            loadUrl("http://127.0.0.1:18923")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
