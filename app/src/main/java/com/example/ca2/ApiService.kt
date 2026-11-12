package com.example.ca2

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

/**
 * Retrofit interface defining the HTTP GET request for Wikipedia REST API endpoints.
 */
interface WikiApiService {
    @GET("page/summary/{title}")
    suspend fun getSummary(
        @Path("title") title: String
    ): Response<WikiSummaryResponse>
}

/**
 * Singleton API Client & Service Layer handling HTTP network requests.
 */
object ApiService {

    private const val WIKI_BASE_URL = "https://en.wikipedia.org/api/rest_v1/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "DestinationGuideApp/1.0 (Android; contact@example.com)")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    private val wikiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WIKI_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val wikiApiService: WikiApiService by lazy {
        wikiRetrofit.create(WikiApiService::class.java)
    }

    /**
     * Curated dataset of popular travel destinations with detailed attributes.
     */
    private val curatedDestinations = mapOf(
        "paris" to DestinationModel(
            name = "Paris",
            country = "France",
            imageUrl = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=1000&q=80",
            description = "Paris, France's capital, is a major European city and a global center for art, fashion, gastronomy, and culture. Its 19th-century cityscape is crisscrossed by wide boulevards and the River Seine.",
            attractions = listOf("Eiffel Tower", "Louvre Museum", "Notre-Dame Cathedral", "Arc de Triomphe", "Sacré-Cœur"),
            bestTimeToVisit = "June to August and September to October for pleasant weather and vibrant festivals.",
            popularActivities = listOf("Seine River Cruise", "Museum Hopping", "Café Hopping in Le Marais", "Wine & Cheese Tasting"),
            travelInfo = "Public transport is comprehensive with the Métro network. The official currency is the Euro (€). Language spoken is French.",
            websiteUrl = "https://en.wikipedia.org/wiki/Paris"
        ),
        "tokyo" to DestinationModel(
            name = "Tokyo",
            country = "Japan",
            imageUrl = "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?auto=format&fit=crop&w=1000&q=80",
            description = "Tokyo, Japan’s bustling capital, mixes ultramodern and traditional elements, from neon-lit skyscrapers to historic temples. The opulent Meiji Shinto Shrine is known for its towering gate and surrounding woods.",
            attractions = listOf("Senso-ji Temple", "Tokyo Tower", "Shibuya Crossing", "Meiji Shrine", "Akihabara"),
            bestTimeToVisit = "March to May (Cherry Blossom season) and September to November.",
            popularActivities = listOf("Exploring Shibuya & Shinjuku", "Tasting authentic Ramen & Sushi", "Visiting teamLab Planets", "Shopping in Ginza"),
            travelInfo = "Suica/Pasmo cards make train travel effortless. Currency: Japanese Yen (¥). Very safe city with exceptional customer service.",
            websiteUrl = "https://en.wikipedia.org/wiki/Tokyo"
        ),
        "new york" to DestinationModel(
            name = "New York City",
            country = "United States",
            imageUrl = "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?auto=format&fit=crop&w=1000&q=80",
            description = "New York City comprises 5 boroughs sitting where the Hudson River meets the Atlantic Ocean. At its core is Manhattan, a densely populated borough that’s among the world’s major commercial, financial and cultural centers.",
            attractions = listOf("Statue of Liberty", "Central Park", "Empire State Building", "Times Square", "Metropolitan Museum of Art"),
            bestTimeToVisit = "April to June and September to November when temperatures are mild.",
            popularActivities = listOf("Broadway Shows", "Walking across Brooklyn Bridge", "Central Park Bike Tour", "High Line Walk"),
            travelInfo = "Subway system runs 24/7. Tipping 18-20% is standard in restaurants. Currency is US Dollar ($).",
            websiteUrl = "https://en.wikipedia.org/wiki/New_York_City"
        ),
        "rome" to DestinationModel(
            name = "Rome",
            country = "Italy",
            imageUrl = "https://images.unsplash.com/photo-1552832230-c0197dd311b5?auto=format&fit=crop&w=1000&q=80",
            description = "Rome, Italy’s capital, is a sprawling, cosmopolitan city with nearly 3,000 years of globally influential art, architecture and culture on display. Ancient ruins such as the Forum and the Colosseum evoke the power of the former Roman Empire.",
            attractions = listOf("Colosseum", "Vatican Museums & St. Peter's", "Trevi Fountain", "Pantheon", "Piazza Navona"),
            bestTimeToVisit = "October to April for fewer crowds and pleasant spring/autumn weather.",
            popularActivities = listOf("Gelato tasting", "Exploring Roman Forum", "Vatican City tour", "Italian cooking class"),
            travelInfo = "Walking is the best way to explore ancient cobblestone streets. Currency: Euro (€).",
            websiteUrl = "https://en.wikipedia.org/wiki/Rome"
        ),
        "bali" to DestinationModel(
            name = "Bali",
            country = "Indonesia",
            imageUrl = "https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=1000&q=80",
            description = "Bali is an Indonesian island known for its forested volcanic mountains, iconic rice paddies, beaches and coral reefs. The island is home to religious sites such as cliffside Uluwatu Temple.",
            attractions = listOf("Uluwatu Temple", "Tegallalang Rice Terraces", "Sacred Monkey Forest", "Tanah Lot", "Mount Batur"),
            bestTimeToVisit = "April to October during the dry season.",
            popularActivities = listOf("Surfing in Kuta/Canggu", "Sunrise hike on Mount Batur", "Spa & Wellness treatments", "Scuba diving"),
            travelInfo = "Scooter rental or private drivers are common. Currency: Indonesian Rupiah (IDR). Visa on Arrival available.",
            websiteUrl = "https://en.wikipedia.org/wiki/Bali"
        ),
        "cairo" to DestinationModel(
            name = "Cairo",
            country = "Egypt",
            imageUrl = "https://images.unsplash.com/photo-1572252821143-035a024857ef?auto=format&fit=crop&w=1000&q=80",
            description = "Cairo, Egypt’s sprawling capital, is set on the Nile River. At its heart is Tahrir Square and the vast Egyptian Museum, a trove of antiquities including royal mummies and gilded King Tutankhamun artifacts.",
            attractions = listOf("Giza Pyramids & Sphinx", "Grand Egyptian Museum", "Khan el-Khalili Bazaar", "Citadel of Saladin", "Nile River"),
            bestTimeToVisit = "October to April when temperatures are comfortable.",
            popularActivities = listOf("Camel ride at the Pyramids", "Felucca sailboat ride on the Nile", "Exploring Islamic Cairo", "Shopping in Old Bazaars"),
            travelInfo = "Uber is widely available in Cairo. Currency: Egyptian Pound (EGP). Respect local dress customs.",
            websiteUrl = "https://en.wikipedia.org/wiki/Cairo"
        ),
        "sydney" to DestinationModel(
            name = "Sydney",
            country = "Australia",
            imageUrl = "https://images.unsplash.com/photo-1506973035872-a4ec16b8e8d9?auto=format&fit=crop&w=1000&q=80",
            description = "Sydney, capital of New South Wales and one of Australia's largest cities, is best known for its harbourfront Sydney Opera House, with a distinctive sail-like design.",
            attractions = listOf("Sydney Opera House", "Harbour Bridge", "Bondi Beach", "Darling Harbour", "Royal Botanic Garden"),
            bestTimeToVisit = "September to November and February to May.",
            popularActivities = listOf("BridgeClimb Sydney", "Surfing at Bondi Beach", "Coastal walk from Bondi to Coogee", "Ferries to Manly"),
            travelInfo = "Opal card or contactless cards work on all public transport. Currency: Australian Dollar (AUD).",
            websiteUrl = "https://en.wikipedia.org/wiki/Sydney"
        ),
        "london" to DestinationModel(
            name = "London",
            country = "United Kingdom",
            imageUrl = "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?auto=format&fit=crop&w=1000&q=80",
            description = "London, the capital of England and the United Kingdom, is a 21st-century city with history stretching back to Roman times. At its centre stand the imposing Houses of Parliament, the iconic 'Big Ben' clock tower and Westminster Abbey.",
            attractions = listOf("Big Ben & Parliament", "London Eye", "Tower of London", "British Museum", "Buckingham Palace"),
            bestTimeToVisit = "March to May and September to November.",
            popularActivities = listOf("West End Theatre Shows", "Visiting Free Museums", "Afternoon Tea", "Exploring Camden Market"),
            travelInfo = "The London Underground (Tube) is fast and convenient. Pay with contactless payment. Currency: British Pound (£).",
            websiteUrl = "https://en.wikipedia.org/wiki/London"
        )
    )

    /**
     * Executes HTTP GET network call to fetch destination details asynchronously.
     * Sends User-Agent header and queries Wikipedia REST API.
     */
    suspend fun fetchDestinationDetails(query: String): Result<DestinationModel> = withContext(Dispatchers.IO) {
        val normalizedQuery = query.trim().lowercase()

        // 1. Check curated matching popular destination first for rich data
        val curatedMatch = curatedDestinations[normalizedQuery]
            ?: curatedDestinations.entries.find { normalizedQuery.contains(it.key) || it.key.contains(normalizedQuery) }?.value

        if (curatedMatch != null) {
            return@withContext Result.success(curatedMatch)
        }

        // 2. Perform live HTTP GET request to Wikipedia REST API for dynamic remote search
        try {
            val encodedTitle = URLEncoder.encode(query.trim().replace(" ", "_"), "UTF-8")
            val response = wikiApiService.getSummary(encodedTitle)

            if (response.isSuccessful && response.body() != null) {
                val wikiData = response.body()!!
                val title = wikiData.title ?: wikiData.displaytitle ?: query
                val extract = wikiData.extract ?: wikiData.description ?: "No detailed description available."
                val imageUrl = wikiData.originalimage?.source ?: wikiData.thumbnail?.source
                ?: "https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=1000&q=80"
                val pageUrl = wikiData.contentUrls?.mobile?.page
                    ?: wikiData.contentUrls?.desktop?.page
                    ?: "https://en.wikipedia.org/wiki/$encodedTitle"
                val country = wikiData.description ?: "Global Destination"

                val destinationModel = DestinationModel(
                    name = title,
                    country = country,
                    imageUrl = imageUrl,
                    description = extract,
                    attractions = listOf("Historic Landmarks", "City Center", "Local Museums", "Cultural Sites", "Scenic Views"),
                    bestTimeToVisit = "Spring (April - June) and Autumn (September - November)",
                    popularActivities = listOf("Sightseeing", "Local Food Tasting", "Photography", "Cultural Tours"),
                    travelInfo = "Accessible via local transit networks. Check local weather and travel advisories prior to visiting.",
                    websiteUrl = pageUrl
                )

                return@withContext Result.success(destinationModel)
            } else {
                return@withContext Result.failure(Exception("Destination '$query' not found (HTTP ${response.code()})"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
}
