package com.example.ca2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.ca2.databinding.ActivitySummaryBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

/**
 * Summary Activity displaying destination details returned by the HTTP GET API request.
 */
class SummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySummaryBinding
    private var currentDestination: DestinationModel? = null
    private var destinationQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        binding.summaryToolbar.setNavigationOnClickListener {
            finish()
        }

        // Get query extra
        destinationQuery = intent.getStringExtra(SearchActivity.EXTRA_QUERY) ?: "Paris"

        // Retry button click
        binding.btnRetry.setOnClickListener {
            fetchDestinationData(destinationQuery)
        }

        // WebView launch button click
        binding.btnVisitWebsite.setOnClickListener {
            currentDestination?.let { destination ->
                val intent = Intent(this, WebViewActivity::class.java).apply {
                    putExtra(WebViewActivity.EXTRA_URL, destination.websiteUrl)
                    putExtra(WebViewActivity.EXTRA_TITLE, destination.name)
                }
                startActivity(intent)
            }
        }

        // Fetch destination data from web API server
        fetchDestinationData(destinationQuery)
    }

    private fun fetchDestinationData(query: String) {
        showLoadingState()

        lifecycleScope.launch {
            val result = ApiService.fetchDestinationDetails(query)
            result.onSuccess { destination ->
                currentDestination = destination
                displayDestinationData(destination)
            }.onFailure { error ->
                showErrorState(error.localizedMessage ?: "Failed to retrieve destination details.")
            }
        }
    }

    private fun showLoadingState() {
        binding.layoutLoading.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE
        binding.layoutContent.visibility = View.GONE
    }

    private fun showErrorState(errorMessage: String) {
        binding.layoutLoading.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.layoutContent.visibility = View.GONE
        binding.tvErrorMessage.text = errorMessage
    }

    private fun displayDestinationData(destination: DestinationModel) {
        binding.layoutLoading.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
        binding.layoutContent.visibility = View.VISIBLE

        // Title and Header
        binding.summaryToolbar.title = destination.name
        binding.tvDestinationName.text = destination.name
        binding.tvCountry.text = destination.country

        // Description
        binding.tvDescription.text = destination.description

        // Best time to visit
        binding.tvBestTimeToVisit.text = destination.bestTimeToVisit

        // Popular activities
        if (destination.popularActivities.isNotEmpty()) {
            binding.tvPopularActivities.text = destination.popularActivities.mapIndexed { index, activity ->
                "${index + 1}. $activity"
            }.joinToString("\n")
        } else {
            binding.tvPopularActivities.text = getString(R.string.default_activities)
        }

        // Travel information
        binding.tvTravelInfo.text = destination.travelInfo

        // Load Destination Image into ImageView using Glide
        Glide.with(this)
            .load(destination.imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.ivDestinationImage)

        // Populate Attractions Chips
        binding.chipGroupAttractions.removeAllViews()
        destination.attractions.forEach { attraction ->
            val chip = Chip(this).apply {
                text = attraction
                isClickable = false
                isCheckable = false
                setChipBackgroundColorResource(R.color.chip_background)
                setTextColor(getColor(R.color.text_primary))
                setChipStrokeColorResource(R.color.chip_stroke)
                chipStrokeWidth = 1.5f
                setChipIconResource(R.drawable.ic_star)
                setChipIconTintResource(R.color.accent)
            }
            binding.chipGroupAttractions.addView(chip)
        }
    }
}
