package com.goodaloe.webbrowser

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private val webView: WebView by lazy {
        findViewById(R.id.web_view)
    }
    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById(R.id.refresh_layout)
    }

    private val addressBar: EditText by lazy {
        findViewById(R.id.address_bar)
    }

    private val homeBtn: ImageButton by lazy {
        findViewById(R.id.home_btn)
    }

    private val backBtn: ImageButton by lazy {
        findViewById(R.id.back_btn)
    }

    private val forwardBtn: ImageButton by lazy {
        findViewById(R.id.forward_btn)
    }

    private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById(R.id.progress_bar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        bindViews()
    }

    override fun onBackPressed() {
        if (webView.canGoBack())
            webView.goBack()
        else
            super.onBackPressed()
    }

    private fun bindViews() {
        addressBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val loadingUrl = v.text.toString()
                if (URLUtil.isNetworkUrl(loadingUrl))
                    webView.loadUrl(loadingUrl)
                else
                    webView.loadUrl("http://${loadingUrl}")
            }
            return@setOnEditorActionListener false
        }
        homeBtn.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }

        forwardBtn.setOnClickListener {
            webView.goForward()
        }

        backBtn.setOnClickListener {
            webView.goBack()
        }

        refreshLayout.setOnRefreshListener {
            webView.reload()
        }
    }

    private fun initViews() {
        webView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
        }
    }

    inner class WebViewClient : android.webkit.WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.show()

        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            refreshLayout.isRefreshing = false
            progressBar.hide()
            backBtn.isEnabled = webView.canGoBack()
            forwardBtn.isEnabled = webView.canGoForward()
            addressBar.setText(url)
        }
    }

    inner class WebChromeClient : android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            progressBar.progress = newProgress
            super.onProgressChanged(view, newProgress)

        }
    }

    companion object {
        private const val DEFAULT_URL = "http://www.google.com"
    }
}