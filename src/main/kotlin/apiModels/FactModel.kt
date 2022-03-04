package apiModels
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName


@Serializable
data class FactModel(
    @SerialName("id")
    val id: String,
    @SerialName("text")
    val text: String,
    @SerialName("source")
    val source: String,
    @SerialName("source_url")
    val sourceUrl: String,
    @SerialName("language")
    val language: String,
    @SerialName("permalink")
    val permalink: String
)