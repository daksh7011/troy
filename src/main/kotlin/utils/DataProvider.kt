package utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object DataProvider {
    fun getBurnData(): List<String> {
        val burnJson = this::class.java.classLoader.getResource("burn.json").readText()
        return Json.decodeFromString(burnJson)
    }
}
