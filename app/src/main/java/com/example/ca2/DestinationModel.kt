package com.example.ca2

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Data model representing detailed destination information.
 */
data class DestinationModel(
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("description") val description: String,
    @SerializedName("attractions") val attractions: List<String> = emptyList(),
    @SerializedName("bestTimeToVisit") val bestTimeToVisit: String,
    @SerializedName("popularActivities") val popularActivities: List<String> = emptyList(),
    @SerializedName("travelInfo") val travelInfo: String,
    @SerializedName("websiteUrl") val websiteUrl: String
) : Serializable

/**
 * Wikipedia REST API Response Data Models
 */
data class WikiSummaryResponse(
    @SerializedName("title") val title: String?,
    @SerializedName("displaytitle") val displaytitle: String?,
    @SerializedName("extract") val extract: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("thumbnail") val thumbnail: WikiImage?,
    @SerializedName("originalimage") val originalimage: WikiImage?,
    @SerializedName("content_urls") val contentUrls: WikiContentUrls?
) : Serializable

data class WikiImage(
    @SerializedName("source") val source: String?,
    @SerializedName("width") val width: Int?,
    @SerializedName("height") val height: Int?
) : Serializable

data class WikiContentUrls(
    @SerializedName("desktop") val desktop: WikiPageUrl?,
    @SerializedName("mobile") val mobile: WikiPageUrl?
) : Serializable

data class WikiPageUrl(
    @SerializedName("page") val page: String?
) : Serializable
