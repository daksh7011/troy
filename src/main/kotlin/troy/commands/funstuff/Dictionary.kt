package troy.commands.funstuff

import dev.kord.core.Kord
import dev.kord.rest.builder.message.embed
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import dev.kordex.core.utils.env
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import troy.apiModels.OwlDictModel
import troy.utils.*

class Dictionary : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "dictionary"

    class DictionaryArguments : Arguments() {
        val word by string {
            name = "word".toKey()
            description = "Which word do you wanna search?".toKey()
        }
    }

    override suspend fun setup() {
        publicSlashCommand(Dictionary::DictionaryArguments) {
            name = "dictionary".toKey()
            description = "Finds definition for given word with image, emoji and examples.".toKey()
            action {
                val url = "$API_BASE_URL${arguments.word}"
                var owlDictModel: OwlDictModel? = null

                // Add timeout to HTTP request to prevent hanging
                val success = withTimeoutOrNull(REQUEST_TIMEOUT_MS) {
                    val result = httpClient.requestAndCatchResponse(
                        block = {
                            get(url) {
                                headers {
                                    append(HttpHeaders.Authorization, "Token ${env(Environment.OWL_DICT_TOKEN)}")
                                }
                            }.body<OwlDictModel>().let {
                                owlDictModel = it
                            }
                            true
                        },
                        notFoundHandler = {
                            respond { content = "No results found for ${arguments.word}" }
                            false
                        },
                        logPrefix = "Failed to fetch dictionary definition"
                    )
                    result ?: false
                } ?: run {
                    commonLogger.error { "Timeout occurred while fetching dictionary definition for ${arguments.word}" }
                    false
                }

                if (success) {
                    // Since we've already checked owlDictModel is not null, we can safely use it
                    owlDictModel?.let { model ->
                        respond {
                            // Check if definitions list is not empty before accessing first element
                            if (model.definitions.isNotEmpty()) {
                                val definition = model.definitions.first()
                                embed {
                                    title = "Definition for ${arguments.word}"
                                    description = "Definition: ${definition.definition}"
                                    image = definition.imageUrl
                                    field {
                                        name = "Type"
                                        value = definition.type.orNotAvailable()
                                        inline = true
                                    }
                                    field {
                                        name = "Emoji"
                                        value = definition.emoji.orNotAvailable()
                                        inline = true
                                    }
                                    field {
                                        name = "Example"
                                        value = definition.example.orNotAvailable()
                                        inline = false
                                    }
                                    footer = kordClient.getEmbedFooter()
                                    timestamp = Clock.System.now()
                                }
                            } else {
                                content = "No definitions found for ${arguments.word}"
                            }
                        }
                    }
                } else if (!success) {
                    respond { content = "Sorry, I couldn't fetch the definition at the moment. Please try again later." }
                }
            }
        }
    }

    companion object {
        private const val API_BASE_URL = "https://owlbot.info/api/v4/dictionary/"
        private const val REQUEST_TIMEOUT_MS = 5000L

        private fun String?.orNotAvailable(): String = this ?: "Not available"
    }
}
