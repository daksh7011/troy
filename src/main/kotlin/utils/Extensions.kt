package utils

import com.kotlindiscord.kord.extensions.utils.env
import core.Credits
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.EmbedBuilder
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.http.*

object Extensions {
    fun Credits.embedUrl(): String = "[$name](${url.encodeURLPath()}) for ($reason)"
    fun Message.isBot(): Boolean = author?.isBot ?: true
    fun Message.isNotBot(): Boolean = isBot().not()
    fun Message.containsF(): Boolean = content.lowercase() == "f"
    fun Message.containsNigga(): Boolean = content.lowercase().contains("nigga")
    fun Message.containsTableFlip(): Boolean = content.lowercase().contains("(╯°□°）╯︵ ┻━┻")
    fun Snowflake.isOwner(): Boolean = asString == env(Environment.OWNER_ID)

    suspend fun <T> HttpClient.requestAndCatch(
        block: suspend HttpClient.() -> T,
        errorHandler: suspend ResponseException.() -> T
    ): T = runCatching { block() }
        .getOrElse {
            when (it) {
                is ResponseException -> it.errorHandler()
                else -> throw it
            }
        }

    suspend fun Message.getEmbedFooter(): EmbedBuilder.Footer {
        val footer = EmbedBuilder.Footer()
        footer.text = "Powered by ${this.kord.getUser(this.kord.selfId)?.username}"
        footer.icon = this.kord.getUser(this.kord.selfId)?.avatar?.defaultUrl
        return footer
    }

    suspend fun Kord.getEmbedFooter(): EmbedBuilder.Footer {
        val footer = EmbedBuilder.Footer()
        footer.text = "Powered by ${this.getUser(this.selfId)?.username}"
        footer.icon = this.getUser(this.selfId)?.avatar?.defaultUrl
        return footer
    }

    fun getTestGuildSnowflake(): Snowflake {
        return Snowflake(
            env(Environment.TEST_GUILD_ID)?.toLong()
                ?: error("Env var TEST_SERVER not provided")
        )
    }
}
