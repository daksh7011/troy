package utils

import core.Credits
import io.ktor.http.*

object Extensions {
    fun Credits.embedUrl(): String {
        return "[$name](${url.encodeURLPath()}) for ($reason)"
    }
}
