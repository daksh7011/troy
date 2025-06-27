package troy.commands.funstuff

import dev.kord.core.Kord
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.embed
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import troy.apiModels.UrbanDictItem
import troy.apiModels.UrbanDictModel
import troy.utils.*
import troy.utils.requestAndCatchResponse

class UrbanDictionary : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "urban"

    class UrbanDictArguments : Arguments() {
        val search by string {
            name = "query".toKey()
            description = "What do you want to search at UrbanDictionary?".toKey()
        }
    }

    override suspend fun setup() {
        publicSlashCommand(UrbanDictionary::UrbanDictArguments) {
            name = "urban".toKey()
            description = "Returns a definition from Urban Dictionary".toKey()
            action {
                var urbanDictModel: UrbanDictModel? = null
                val search = arguments.search.encodeQuery()

                // Add timeout to an HTTP request to prevent hanging
                val success = withTimeoutOrNull(REQUEST_TIMEOUT_MS) {
                    val result = httpClient.requestAndCatchResponse(
                        block = {
                            urbanDictModel = get("$URBAN_API_URL=$search").body()
                            true
                        },
                        notFoundHandler = {
                            this@action.respond {
                                content = "${NOT_FOUND_MESSAGE}${arguments.search}"
                            }
                            false
                        },
                        logPrefix = "Failed to fetch urban dictionary definition"
                    )
                    result ?: false
                } ?: run {
                    commonLogger.error { "Timeout occurred while fetching urban dictionary definition for: ${arguments.search}" }
                    false
                }

                if (success && urbanDictModel?.list.isNotNullNorEmpty()) {
                    urbanDictModel?.list?.firstOrNull()?.let { urbanDictItem ->
                        val definitionCount = urbanDictItem.definition.count()
                        val exampleCount = urbanDictItem.example.count()
                        val authorCount = urbanDictItem.author.count()

                        if (definitionCount + exampleCount + authorCount > MAX_CHARS) {
                            respond {
                                content = "$TOO_LONG_MESSAGE${urbanDictItem.permalink}"
                            }
                        } else {
                            respond {
                                embed {
                                    setupUrbanDictEmbed(arguments, urbanDictItem)
                                }
                            }
                        }
                    }
                } else if (!success) {
                    respond { content = ERROR_MESSAGE }
                }
            }
        }
    }

    private suspend fun EmbedBuilder.setupUrbanDictEmbed(arguments: UrbanDictArguments, urbanDictItem: UrbanDictItem) {
        title = EMBED_TITLE
        field {
            name = arguments.search
            value = urbanDictItem.definition
        }
        field {
            name = EXAMPLE_FIELD_NAME
            value = urbanDictItem.example
        }
        field {
            name = AUTHOR_FIELD_NAME
            value = urbanDictItem.author
            inline = true
        }
        footer = kordClient.getEmbedFooter()
        timestamp = Clock.System.now()
    }

    companion object {
        private const val MAX_CHARS = 2000
        private const val URBAN_API_URL = "https://api.urbandictionary.com/v0/define?term"
        private const val REQUEST_TIMEOUT_MS = 5000L
        private const val EMBED_TITLE = "Urban Dictionary"
        private const val EXAMPLE_FIELD_NAME = "Example"
        private const val AUTHOR_FIELD_NAME = "Added by"
        private const val NOT_FOUND_MESSAGE = "Can't find the data for "
        private const val TOO_LONG_MESSAGE = "Can not send the response since it is too long, Here is the link to that page for you instead.\n"
        private const val ERROR_MESSAGE = "Sorry, I couldn't fetch the definition at the moment. Please try again later."
    }
}
