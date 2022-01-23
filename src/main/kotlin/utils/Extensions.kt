package utils

import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.EmbedBuilder
import io.getunleash.DefaultUnleash
import io.getunleash.Unleash
import io.getunleash.util.UnleashConfig
import io.ktor.client.*
import io.ktor.client.features.*
import org.jetbrains.exposed.sql.Database

object Extensions {
    fun Message.isBot(): Boolean = author?.isBot ?: true
    fun Message.isNotBot(): Boolean = isBot().not()
    fun Message.containsF(): Boolean = content.lowercase() == "f"
    fun Message.containsNigga(): Boolean = content.lowercase().contains("nigga")
    fun Message.containsTableFlip(): Boolean = content.lowercase().contains("(╯°□°）╯︵ ┻━┻")
    fun Snowflake.isOwner(): Boolean = asString == env(Environment.OWNER_ID)
    fun Snowflake.isGirlfriend(): Boolean = asString == env(Environment.GIRLFRIEND_ID)

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
        footer.icon = this.kord.getUser(this.kord.selfId)?.avatar?.url
        return footer
    }

    suspend fun Kord.getEmbedFooter(): EmbedBuilder.Footer {
        val footer = EmbedBuilder.Footer()
        footer.text = "Powered by ${this.getUser(this.selfId)?.username}"
        footer.icon = this.getUser(this.selfId)?.avatar?.url
        return footer
    }

    fun getTestGuildSnowflake(): Snowflake {
        return Snowflake(
            env(Environment.TEST_GUILD_ID).toLong()
        )
    }

    fun provideUnleashClient(): Unleash? {
        if (env(Environment.IS_DEBUG).toBoolean().not()) {
            val config = UnleashConfig.builder()
                .appName("Troy")
                .instanceId(env(Environment.UNLEASH_INSTANCE_ID))
                .unleashAPI(env(Environment.UNLEASH_URL))
                .build()
            return DefaultUnleash(config)
        }
        return null
    }

    fun connectToDatabase() {
        if (env(Environment.IS_DEBUG).toBoolean()) {
            Database.connect("jdbc:sqlite:src/main/kotlin/data/troy.db", "org.sqlite.JDBC")
        } else {
            Database.connect("jdbc:sqlite:troy.db", "org.sqlite.JDBC")
        }
    }
}
