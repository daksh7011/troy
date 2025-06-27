package troy.utils

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.PublicSlashCommandContext
import dev.kordex.core.components.forms.ModalForm
import dev.kordex.core.utils.env
import `in`.technowolf.linksDetekt.detector.LinksDetektor
import `in`.technowolf.linksDetekt.detector.LinksDetektorOptions
import io.getunleash.DefaultUnleash
import io.getunleash.Unleash
import io.getunleash.util.UnleashConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Checks if the message was sent by a bot.
 *
 * @return true if the message author is a bot, false otherwise
 */
fun Message.isBot(): Boolean = author?.isBot != false

/**
 * Checks if the message was not sent by a bot.
 *
 * @return true if the message author is not a bot, false otherwise
 */
fun Message.isNotBot(): Boolean = isBot().not()

/**
 * Checks if the message content is exactly "f" (case-insensitive).
 *
 * @return true if the message content is "f", false otherwise
 */
fun Message.containsF(): Boolean = content.lowercase() == "f"

/**
 * Checks if the message content contains the word "nigga" (case-insensitive).
 *
 * @return true if the message contains the specified word, false otherwise
 */
fun Message.containsNigga(): Boolean = content.lowercase().contains("nigga")

/**
 * Checks if the message content contains a table flip emoji (case-insensitive).
 *
 * @return true if the message contains a table flip emoji, false otherwise
 */
fun Message.containsTableFlip(): Boolean = content.lowercase().contains("(╯°□°）╯︵ ┻━┻")

/**
 * Checks if the message content contains the word "bullshit" (case-insensitive).
 *
 * @return true if the message contains the specified word, false otherwise
 */
fun Message.containsBs(): Boolean = content.lowercase().contains("bullshit")

/**
 * Checks if the Snowflake ID represents the bot owner.
 *
 * @return true if the Snowflake ID matches the owner ID from environment, false otherwise
 */
fun Snowflake.isOwner(): Boolean = toString() == env(Environment.OWNER_ID)

/**
 * Checks if the Snowflake ID represents the bot owner's girlfriend.
 *
 * @return true if the Snowflake ID matches the girlfriend ID from environment, false otherwise
 */
fun Snowflake.isGirlfriend(): Boolean = toString() == env(Environment.GIRLFRIEND_ID)

/**
 * Executes an HTTP request and handles any exceptions with a custom error handler.
 *
 * @param T The return type of both the request and error handler
 * @param block The HTTP request to execute
 * @param errorHandler A function to handle exceptions if they occur
 * @return The result of the request or the error handler
 */
suspend fun <T> HttpClient.requestAndCatch(
    block: suspend HttpClient.() -> T,
    errorHandler: suspend Throwable.() -> T
): T = runCatching { block() }
    .getOrElse {
        errorHandler(it)
    }

/**
 * Creates a standardized embed footer for a message.
 *
 * @return An EmbedBuilder.Footer with the bot's username and avatar
 */
suspend fun Message.getEmbedFooter(): EmbedBuilder.Footer {
    val footer = EmbedBuilder.Footer()
    footer.text = "Powered by ${this.kord.getUser(this.kord.selfId)?.username}"
    footer.icon = this.kord.getUser(this.kord.selfId)?.avatar?.cdnUrl?.toUrl()
    return footer
}

/**
 * Creates a standardized embed footer for a Kord instance.
 *
 * @return An EmbedBuilder.Footer with the bot's username and avatar
 */
suspend fun Kord.getEmbedFooter(): EmbedBuilder.Footer {
    val footer = EmbedBuilder.Footer()
    footer.text = "Powered by ${this.getUser(this.selfId)?.username}"
    footer.icon = this.getUser(this.selfId)?.avatar?.cdnUrl?.toUrl()
    return footer
}

/**
 * Retrieves the Snowflake ID for the test guild from environment variables.
 *
 * @return A Snowflake object representing the test guild ID
 */
fun getTestGuildSnowflake(): Snowflake {
    return Snowflake(
        env(Environment.TEST_GUILD_ID).toLong(),
    )
}

/**
 * Provides a configured Unleash client for feature flag management.
 *
 * The client is configured with the application name "Troy" and
 * instance ID and API URL from environment variables.
 *
 * The client is lazily initialized to improve performance.
 */
val unleashClient: Unleash by lazy {
    val config = UnleashConfig.builder()
        .appName("Troy")
        .instanceId(env(Environment.UNLEASH_INSTANCE_ID))
        .unleashAPI(env(Environment.UNLEASH_URL))
        .build()
    DefaultUnleash(config)
}

/**
 * Formats a nullable string with Discord's bold markdown syntax.
 *
 * @return A string surrounded by "**" for Discord bold formatting, or an empty string if the input is null
 */
fun String?.bold(): String = if (this != null) "**$this**" else ""

/**
 * Formats a nullable string with Discord's italic markdown syntax.
 *
 * @return A string surrounded by "*" for Discord italic formatting, or an empty string if the input is null
 */
fun String?.italic(): String = if (this != null) "*$this*" else ""

/**
 * Checks if a string is either empty or contains only whitespace.
 *
 * @return true if the string is empty or contains only whitespace, false otherwise
 */
fun String.isEmptyOrBlank(): Boolean = this.isBlank() || this.isEmpty()

/**
 * Provides a safe way to handle nullable integers by returning 0 for null values.
 *
 * @return The integer value if not null, or 0 if null
 */
fun Int?.orZero(): Int = this ?: 0

/**
 * Provides a simplified way to respond to a slash command with text content.
 *
 * @param T The type of Arguments for the slash command
 * @param M The type of ModalForm for the slash command
 * @param text The text content to send in the response
 * @return The result of the respond call
 */
suspend fun <T : Arguments, M : ModalForm> PublicSlashCommandContext<T, M>.respond(text: String) = this.respond {
    content = text
}

/**
 * Extracts all links from a string using various detection methods.
 *
 * This function uses LinksDetektor with different options to extract links from various
 * contexts including plain text, brackets, quotes, JSON, JavaScript, XML, and HTML.
 *
 * @return A distinct list of domain names extracted from the string
 */
fun String.extractLinksFromMessage(): List<String> {
    // Define all options to be used
    val options = listOf(
        LinksDetektorOptions.Default,
        LinksDetektorOptions.BRACKET_MATCH,
        LinksDetektorOptions.QUOTE_MATCH,
        LinksDetektorOptions.SINGLE_QUOTE_MATCH,
        LinksDetektorOptions.JSON,
        LinksDetektorOptions.JAVASCRIPT,
        LinksDetektorOptions.XML,
        LinksDetektorOptions.HTML
    )

    // Use sequence for more efficient processing
    return options.asSequence()
        .flatMap { option ->
            LinksDetektor(this, option).detect().asSequence().mapNotNull { it.host }
        }
        .distinct()
        .toList()
}

/**
 * Builds a formatted list of domains with numbering and italic formatting.
 *
 * This function takes a collection of domain strings and formats them into a numbered list
 * where each item is italicized for Discord display.
 *
 * @return A formatted string with numbered and italicized domain entries, or an empty string if the collection is empty
 */
fun Collection<String>.buildFormattedDomainList(): String {
    if (isEmpty()) return ""

    // Use a StringBuilder for efficient string building
    val result = StringBuilder()
    forEachIndexed { index, domain ->
        // Format each line with italic markdown (asterisks)
        result.append("*${index + 1}. $domain*\n")
    }

    return result.toString()
}

/**
 * A pre-configured HTTP client with JSON content negotiation.
 *
 * This client is configured with ContentNegotiation and JSON serialization settings
 * for consistent HTTP requests throughout the application.
 */
val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            },
        )
    }
}

/**
 * Encodes a string for use in URL query parameters.
 *
 * @return The URL-encoded string using UTF-8 encoding
 */
fun String.encodeQuery(): String = URLEncoder.encode(this, "utf-8")

/**
 * A shared logger instance for general logging throughout the application.
 */
val commonLogger = KotlinLogging.logger("in.technowolf.troy.commonLogger")

/**
 * Checks if the list is not null and not empty.
 *
 * This function uses contracts to help the compiler with smart casting.
 * When this function returns true, the compiler knows that the receiver is not null.
 *
 * @return true if the list is not null and contains at least one element, false otherwise
 */
@OptIn(ExperimentalContracts::class)
fun <T> List<T>?.isNotNullNorEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullNorEmpty != null)
    }
    return this != null && this.isNotEmpty()
}
