package commands.funstuff

import apiModels.OwlDictModel
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.core.Kord
import dev.kord.rest.builder.message.create.embed
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import org.koin.core.logger.Level
import utils.Environment
import utils.getEmbedFooter
import utils.httpClient
import utils.requestAndCatch

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
        publicSlashCommand(::DictionaryArguments) {
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
                    when (response.status) {
                        HttpStatusCode.NotFound -> {
                            respond { content = "No results found for ${arguments.word}" }
                        }

                        else -> getKoin().logger.log(Level.ERROR, localizedMessage)
                    }
                })
            }
        }
    }
}

private fun String?.orNotAvailable(): String {
    return this ?: "Not available"
}
