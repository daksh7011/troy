package troy.apiModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UrbanDictModel(
    @SerialName("list")
    val list: List<UrbanDictItem>
)

@Serializable
data class UrbanDictItem(
    @SerialName("author")
    val author: String,
    @SerialName("current_vote")
    val currentVote: String,
    @SerialName("definition")
    val definition: String,
    @SerialName("example")
    val example: String,
    @SerialName("permalink")
    val permalink: String,
    @SerialName("thumbs_down")
    val thumbsDown: Int,
    @SerialName("thumbs_up")
    val thumbsUp: Int,
    @SerialName("word")
    val word: String,
    @SerialName("written_on")
    val writtenOn: String
)
