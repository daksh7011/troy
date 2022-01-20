package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhishingDomainModel(
    @SerialName("domains")
    val domains: List<String>
)
