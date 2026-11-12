package com.example.ca2

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.ca2.databinding.ActivityWebviewBinding

/**
 * Screen 2 – In-App WebView & Navigation Activity.
 * Configures WebView with JS enabled, progress bar, 4 bottom buttons (Back, Forward, Refresh, Home),
 * custom WebViewClient (shouldOverrideUrlLoading = false), and hardware back navigation.
 */
class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebviewBinding
    private var targetUrl: String = "https://en.wikipedia.org"
    private var destinationTitle: String = "Destination Guide Web"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Extract Intent Extras
        targetUrl = intent.getStringExtra(EXTRA_URL) ?: "https://en.wikipedia.org"
        destinationTitle = intent.getStringExtra(EXTRA_TITLE) ?: "Destination Guide Web"

        // Setup Toolbar
        binding.webToolbar.title = destinationTitle
        binding.webToolbar.setNavigationOnClickListener {
            handleBackPress()
        }

        // Configure WebView Settings
        binding.webView.apply {
            settings.javaScriptEnabled = true // Enable JavaScript
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false

            // Attach Custom WebViewClient
            webViewClient = object : WebViewClient() {
                // Keep all link clicks inside the app
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding.webProgressBar.visibility = View.VISIBLE
                    updateNavigationControls()
                    url?.let { binding.tvWebStatus.text = it }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.webProgressBar.visibility = View.GONE
                    updateNavigationControls()
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    if (request?.isForMainFrame == true) {
                        Toast.makeText(
                            this@WebViewActivity,
                            "Failed to load web page. Check internet connection.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            // WebChromeClient to track loading progress
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    binding.webProgressBar.progress = newProgress
                    if (newProgress == 100) {
                        binding.webProgressBar.visibility = View.GONE
                    } else {
                        binding.webProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        }

        // Wire 4 Navigation Buttons:
        // 1. Back: calls webView.goBack() if webView.canGoBack() is true
        binding.btnWebBack.setOnClickListener {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            }
        }

        // 2. Forward: calls webView.goForward() if webView.canGoForward() is true
        binding.btnWebForward.setOnClickListener {
            if (binding.webView.canGoForward()) {
                binding.webView.goForward()
            }
        }

        // 3. Refresh: calls webView.reload()
        binding.btnWebRefresh.setOnClickListener {
            binding.webView.reload()
        }

        // 4. Home: finishes WebViewActivity (navigates cleanly back to MainActivity)
        binding.btnWebHome.setOnClickListener {
            finish()
        }

        // Register OnBackPressedCallback for device Back button hardware/gesture override
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        })

        // Load Article Website URL
        binding.webView.loadUrl(targetUrl)
    }

    private fun handleBackPress() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            finish()
        }
    }

    private fun updateNavigationControls() {
        val canGoBack = binding.webView.canGoBack()
        val canGoForward = binding.webView.canGoForward()

        binding.btnWebBack.isEnabled = canGoBack
        binding.btnWebBack.alpha = if (canGoBack) 1.0f else 0.4f

        binding.btnWebForward.isEnabled = canGoForward
        binding.btnWebForward.alpha = if (canGoForward) 1.0f else 0.4f
    }

    companion object {
        const val EXTRA_URL = "extra_web_url"
        const val EXTRA_TITLE = "extra_web_title"
    }
}
