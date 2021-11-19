package models
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName

class NoodsModel : ArrayList<NoodsModelItem>()

@Serializable
data class NoodsModelItem(
    @SerialName("categoryName")
    val categoryName: List<String>,
    @SerialName("fullName")
    val fullName: String,
    @SerialName("value")
    val value: List<String>
)