package apiModels

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Rule34Model(
    @SerialName("preview_url")
    val previewUrl: String,
    @SerialName("sample_url")
    val sampleUrl: String,
    @SerialName("file_url")
    val fileUrl: String,
    @SerialName("directory")
    val directory: Int,
    @SerialName("hash")
    val hash: String,
    @SerialName("height")
    val height: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("image")
    val image: String,
    @SerialName("change")
    val change: Int,
    @SerialName("owner")
    val owner: String,
    @SerialName("parent_id")
    val parentId: Int,
    @SerialName("rating")
    val rating: String,
    @SerialName("sample")
    val sample: Int,
    @SerialName("sample_height")
    val sampleHeight: Int,
    @SerialName("sample_width")
    val sampleWidth: Int,
    @SerialName("score")
    val score: Int,
    @SerialName("tags")
    val tags: String,
    @SerialName("width")
    val width: Int
)
