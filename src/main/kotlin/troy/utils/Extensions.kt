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
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
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
 * Executes an HTTP request and handles various response scenarios.
 *
 * @param identifier Optional identifier for logging purposes. If null, "Unknown" will be used.
 * @param block The HTTP request to execute.
 * @param notFoundHandler Optional handler for 404 Not Found responses.
 * @param badRequestHandler Optional handler for 400 Bad Request responses.
 * @param otherStatusHandler Optional handler for other HTTP status codes.
 * @param logPrefix Prefix for error log messages.
 * @return The result of the HTTP request, or null if an error occurred.
 */
suspend fun <T> HttpClient.requestAndCatchResponse(
    identifier: String,
    block: suspend HttpClient.() -> T,
    notFoundHandler: (suspend () -> T)? = null,
    badRequestHandler: (suspend () -> T)? = null,
    otherStatusHandler: (suspend (HttpStatusCode) -> T)? = null,
    logPrefix: String = "Request failed"
): T? {
    return runCatching {
        block()
    }.fold(
        onSuccess = {
            commonLogger.info { "$identifier executed an API call with success" }
            it
        },
        onFailure = { exception ->
            commonLogger.info { "$identifier executed an API call with failure" }
            handleRequestException(exception, notFoundHandler, badRequestHandler, otherStatusHandler, logPrefix)
        }
    )
}

/**
 * Handles exceptions from HTTP requests.
 *
 * @param exception The exception that occurred.
 * @param notFoundHandler Optional handler for 404 Not Found responses.
 * @param badRequestHandler Optional handler for 400 Bad Request responses.
 * @param otherStatusHandler Optional handler for other HTTP status codes.
 * @param logPrefix Prefix for error log messages.
 * @return The result from the appropriate handler, or null if no handler is provided.
 */
private suspend fun <T> handleRequestException(
    exception: Throwable,
    notFoundHandler: (suspend () -> T)?,
    badRequestHandler: (suspend () -> T)?,
    otherStatusHandler: (suspend (HttpStatusCode) -> T)?,
    logPrefix: String
): T? {
    return when (exception) {
        is ResponseException -> handleResponseException(
            exception,
            notFoundHandler,
            badRequestHandler,
            otherStatusHandler,
            logPrefix,
        )
        else -> {
            commonLogger.error { "$logPrefix: ${exception.localizedMessage}" }
            null
        }
    }
}

/**
 * Handles ResponseException based on HTTP status code.
 *
 * @param exception The ResponseException that occurred.
 * @param notFoundHandler Optional handler for 404 Not Found responses.
 * @param badRequestHandler Optional handler for 400 Bad Request responses.
 * @param otherStatusHandler Optional handler for other HTTP status codes.
 * @param logPrefix Prefix for error log messages.
 * @return The result from the appropriate handler, or null if no handler is provided.
 */
private suspend fun <T> handleResponseException(
    exception: ResponseException,
    notFoundHandler: (suspend () -> T)?,
    badRequestHandler: (suspend () -> T)?,
    otherStatusHandler: (suspend (HttpStatusCode) -> T)?,
    logPrefix: String
): T? {
    return when (exception.response.status) {
        HttpStatusCode.NotFound -> notFoundHandler?.invoke() ?: run {
            commonLogger.error { "$logPrefix: Not Found - ${exception.localizedMessage}" }
            null
        }
        HttpStatusCode.BadRequest -> badRequestHandler?.invoke() ?: run {
            commonLogger.error { "$logPrefix: Bad Request - ${exception.localizedMessage}" }
            null
        }
        else -> otherStatusHandler?.invoke(exception.response.status) ?: run {
            commonLogger.error { "$logPrefix: ${exception.response.status} - ${exception.localizedMessage}" }
            null
        }
    }
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
 * This function uses LinksDetektor with all available options to extract links from various
 * contexts including plain text, brackets, quotes, JSON, JavaScript, XML, and HTML.
 *
 * @return A distinct list of domain names extracted from the string
 */
fun String.extractLinksFromMessage(): List<String> {
    // Use all available options
    // Use sequence for more efficient processing
    return LinksDetektorOptions.entries.asSequence()
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
val httpClient: HttpClient = HttpClient {
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
