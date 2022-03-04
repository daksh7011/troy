package apiModels
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName

@Serializable
data class OwlDictModel(
    @SerialName("definitions")
    val definitions: List<Definition>,
    @SerialName("word")
    val word: String,
    @SerialName("pronunciation")
    val pronunciation: String?,
)

@Serializable
data class Definition(
    @SerialName("type")
    val type: String,
    @SerialName("definition")
    val definition: String,
    @SerialName("example")
    val example: String?,
    @SerialName("image_url")
    val imageUrl: String?,
    @SerialName("emoji")
    val emoji: String?,
)
