package com.example.octo.printstudio

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.view.Window
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient

class Stream : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideStatusBar()

        setContentView(R.layout.activity_stream)

        var webView = findViewById<WebView>(R.id.webview)
        webView.webViewClient = WebViewClient()
        var url = "http://80.210.72.202:63500/webcam/?action=stream"
        webView.getSettings().loadWithOverviewMode = true
        webView.getSettings().useWideViewPort = true

        webView.loadUrl(url)

    }


    private fun hideStatusBar(){
        if (Build.VERSION.SDK_INT >= 16) {
            window.setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
            window.decorView.systemUiVisibility = 3328;
        }else{
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}
