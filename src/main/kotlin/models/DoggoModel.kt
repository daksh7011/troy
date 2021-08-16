package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DoggoModel(
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: String
)
