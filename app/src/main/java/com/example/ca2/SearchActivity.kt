package com.example.ca2

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ca2.databinding.ActivitySearchBinding

/**
 * Search Activity providing destination search input and popular suggestions.
 */
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        binding.searchToolbar.setNavigationOnClickListener {
            finish()
        }

        // Handle Search Button Click
        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        // Handle Keyboard Search Action
        binding.etSearchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        // Popular Destination Chips Listeners
        binding.chipParis.setOnClickListener { navigateToSummary("Paris") }
        binding.chipTokyo.setOnClickListener { navigateToSummary("Tokyo") }
        binding.chipNewYork.setOnClickListener { navigateToSummary("New York") }
        binding.chipRome.setOnClickListener { navigateToSummary("Rome") }
        binding.chipBali.setOnClickListener { navigateToSummary("Bali") }
        binding.chipCairo.setOnClickListener { navigateToSummary("Cairo") }
        binding.chipSydney.setOnClickListener { navigateToSummary("Sydney") }
        binding.chipLondon.setOnClickListener { navigateToSummary("London") }
    }

    private fun performSearch() {
        val query = binding.etSearchQuery.text?.toString()?.trim() ?: ""
        if (query.isEmpty()) {
            binding.searchTextInputLayout.error = "Please enter a destination name"
            Toast.makeText(this, "Please enter a destination to search", Toast.LENGTH_SHORT).show()
        } else {
            binding.searchTextInputLayout.error = null
            navigateToSummary(query)
        }
    }

    private fun navigateToSummary(query: String) {
        val intent = Intent(this, SummaryActivity::class.java).apply {
            putExtra(EXTRA_QUERY, query)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_QUERY = "extra_destination_query"
    }
}
