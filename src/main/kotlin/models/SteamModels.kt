package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SteamSearchModel(
    @SerialName("items")
    val gameItems: List<GameItem>? = null,
)

@Serializable
data class GameItem(
    @SerialName("id")
    val id: Int?
)

@Serializable
data class Platforms(
    @SerialName("linux")
    val linux: Boolean,
    @SerialName("mac")
    val mac: Boolean,
    @SerialName("windows")
    val windows: Boolean
)

@Serializable
data class SteamGameModel(
    @SerialName("about_the_game")
    val aboutTheGame: String,
    @SerialName("detailed_description")
    val detailedDescription: String,
    @SerialName("developers")
    val developers: List<String>,
    @SerialName("header_image")
    val headerImage: String,
    @SerialName("is_free")
    val isFree: Boolean,
    @SerialName("dlc")
    val dlc: List<Int>? = null,
    val name: String,
    @SerialName("platforms")
    val platforms: Platforms,
    @SerialName("price_overview")
    val priceOverview: PriceOverview,
    @SerialName("publishers")
    val publishers: List<String>,
    @SerialName("recommendations")
    val recommendations: Recommendations,
    @SerialName("release_date")
    val releaseDate: ReleaseDate,
    @SerialName("required_age")
    val requiredAge: Int,
    @SerialName("short_description")
    val shortDescription: String,
    @SerialName("steam_appid")
    val steamAppid: Int,
    @SerialName("supported_languages")
    val supportedLanguages: String,
    @SerialName("type")
    val type: String,
    @SerialName("website")
    val website: String
)

@Serializable
data class PriceOverview(
    @SerialName("currency")
    val currency: String,
    @SerialName("discount_percent")
    val discountPercent: Int,
    @SerialName("final")
    val `final`: Int,
    @SerialName("final_formatted")
    val finalFormatted: String,
    @SerialName("initial")
    val initial: Int,
    @SerialName("initial_formatted")
    val initialFormatted: String
)

@Serializable
data class Recommendations(
    @SerialName("total")
    val total: Int
)

@Serializable
data class ReleaseDate(
    @SerialName("coming_soon")
    val comingSoon: Boolean,
    @SerialName("date")
    val date: String
)
