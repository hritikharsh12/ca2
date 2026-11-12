package com.example.ca2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.ca2.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

/**
 * Main Activity providing Search & Summary features (Screen 1).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentArticleUrl: String? = null
    private var currentDestinationTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolbar)

        // Handle Search Button Click
        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        // Handle Keyboard Search Action
        binding.etSearchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        // Handle Read More Button Click
        binding.btnReadMore.setOnClickListener {
            currentArticleUrl?.let { url ->
                val intent = Intent(this, WebViewActivity::class.java).apply {
                    putExtra(WebViewActivity.EXTRA_URL, url)
                    putExtra(WebViewActivity.EXTRA_TITLE, currentDestinationTitle ?: "Destination Guide")
                }
                startActivity(intent)
            }
        }

        // Secondary CTA to Explore Popular Getaways (SearchActivity)
        binding.btnStartExploring.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // Load initial default destination ("Paris") on startup
        fetchDestination("Paris")
    }

    private fun performSearch() {
        val query = binding.etSearchInput.text?.toString()?.trim() ?: ""
        if (query.isEmpty()) {
            binding.searchTextInputLayout.error = "Please enter a destination name"
            Toast.makeText(this, "Please enter a destination name to search", Toast.LENGTH_SHORT).show()
        } else {
            binding.searchTextInputLayout.error = null
            fetchDestination(query)
        }
    }

    private fun fetchDestination(query: String) {
        // Show progress bar and hide read more button during fetch
        binding.progressBar.visibility = View.VISIBLE
        binding.btnReadMore.visibility = View.GONE

        lifecycleScope.launch {
            val result = ApiService.fetchDestinationDetails(query)

            binding.progressBar.visibility = View.GONE

            result.onSuccess { destination ->
                currentArticleUrl = destination.websiteUrl
                currentDestinationTitle = destination.name

                // Update UI text fields
                binding.tvDestinationTitle.text = destination.name
                binding.tvDestinationDescription.text = destination.description

                // Load thumbnail image using Glide
                Glide.with(this@MainActivity)
                    .load(destination.imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(binding.ivDestinationThumbnail)

                // Reveal Read More button once data loads
                binding.btnReadMore.visibility = View.VISIBLE
            }.onFailure { error ->
                Toast.makeText(
                    this@MainActivity,
                    error.localizedMessage ?: "Destination not found or network connection error.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
