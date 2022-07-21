package apiModels

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class PunsModel(
    @SerialName("id")
    val id: String,
    @SerialName("joke")
    val joke: String,
    @SerialName("status")
    val status: Int
)
