package troy.commands.funstuff

import dev.kord.core.Kord
import dev.kord.rest.builder.message.embed
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.utils.env
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import troy.apiModels.OwlDictModel
import troy.utils.Environment
import troy.utils.commonLogger
import troy.utils.getEmbedFooter
import troy.utils.httpClient
import troy.utils.requestAndCatch

class Dictionary : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "dictionary"

    class DictionaryArguments : Arguments() {
        val word by string {
            name = "word"
            description = "Which word do you wanna search?"
        }
    }

    override suspend fun setup() {
        publicSlashCommand(Dictionary::DictionaryArguments) {
            name = "dictionary"
            description = "Finds definition for given word with image, emoji and examples."
            action {
                val url = "https://owlbot.info/api/v4/dictionary/" + arguments.word
                httpClient.requestAndCatch({
                    get(url) {
                        headers {
                            append(HttpHeaders.Authorization, "Token ${env(Environment.OWL_DICT_TOKEN)}")
                        }
                    }.body<OwlDictModel>().let {
                        respond {
                            val definition = it.definitions.first()
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
                        }
                    }
                }, {
                    if (response.status == HttpStatusCode.NotFound) {
                        respond { content = "No results found for ${arguments.word}" }
                    } else {
                        commonLogger.error { localizedMessage }
                    }
                })
            }
        }
    }
}

private fun String?.orNotAvailable(): String = this ?: "Not available"
