package utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import models.NoodsModel
import models.NoodsModelItem

object DataProvider {
    fun getBurnData(): List<String> {
        val burnJson = this::class.java.classLoader.getResource("burn.json").readText()
        return Json.decodeFromString(burnJson)
    }
    fun getNoodsData(): List<NoodsModelItem> {
        val noodJson = this::class.java.classLoader.getResource("noods.json").readText()
        return Json.decodeFromString(noodJson)
    }
}
