package troy.apiModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PunsModel(
    @SerialName("id")
    val id: String,
    @SerialName("joke")
    val joke: String,
    @SerialName("status")
    val status: Int
)
