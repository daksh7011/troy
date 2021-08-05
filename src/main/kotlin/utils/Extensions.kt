package utils

import com.kotlindiscord.kord.extensions.utils.env
import core.Credits
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Message
import io.ktor.http.*

object Extensions {
    fun Credits.embedUrl(): String = "[$name](${url.encodeURLPath()}) for ($reason)"
    fun Message.isBot(): Boolean = author?.isBot ?: true
    fun Message.isNotBot(): Boolean = isBot().not()
    fun Message.containsF(): Boolean = content.lowercase() == "f"
    fun Message.containsNigga(): Boolean = content.lowercase().contains("nigga")
    fun Snowflake.isOwner(): Boolean = asString == env(Environment.OWNER_ID)
}
