package commands.`fun`

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescedString
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.embed
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.Clock
import models.UrbanDictItem
import models.UrbanDictModel
import org.koin.core.component.inject
import org.koin.core.logger.Level
import utils.Extensions.getEmbedFooter
import utils.Extensions.requestAndCatch

class UrbanDictionary : Extension() {

    private val kordClient: Kord by inject()

    private val httpClient = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private val urbanApiUrl = "https://api.urbandictionary.com/v0/define?term="

    override val name: String
        get() = "urban"

    class UrbanDictArguments : Arguments() {
        val search by coalescedString("query", "What do you want to search at UrbanDictionary?")
    }

    override suspend fun setup() {
        chatCommand(::UrbanDictArguments) {
            name = "urban"
            description = "Returns a definition from Urban Dictionary"
            aliases = arrayOf("ud")
            action {
                var urbanDictModel: UrbanDictModel? = null
                httpClient.requestAndCatch({
                    urbanDictModel = this.get(urbanApiUrl + arguments.search)
                }, {
                    when (response.status) {
                        HttpStatusCode.NotFound -> {
                            this@action.message.respond("Can't the data for ${arguments.search}")
                        }
                        else -> getKoin().logger.log(Level.ERROR, localizedMessage)
                    }
                })
                urbanDictModel?.let {
                    if (it.list.isNotEmpty()) {
                        val urbanDictItem = it.list.first()
                        val definitionCount = urbanDictItem.definition.count()
                        val exampleCount = urbanDictItem.example.count()
                        val authorCount = urbanDictItem.author.count()
                        if (definitionCount + exampleCount + authorCount > 2000) {
                            channel.createMessage(
                                "Can not send the response since it is too long, " +
                                        "Here is the link to that page for you instead.\n" +
                                        urbanDictItem.permalink
                            )
                        } else {
                            channel.createEmbed {
                                setupUrbanDictEmbed(arguments, urbanDictItem)
                            }
                        }
                    }
                }
            }
        }

        publicSlashCommand(::UrbanDictArguments) {
            name = "urban"
            description = "Returns a definition from Urban Dictionary"
            action {
                var urbanDictModelForSlashCommand: UrbanDictModel? = null
                httpClient.requestAndCatch({
                    urbanDictModelForSlashCommand = this.get(urbanApiUrl + arguments.search)
                }, {
                    when (response.status) {
                        HttpStatusCode.NotFound -> {
                            this@action.respond { content = "Can't the data for ${arguments.search}" }
                        }
                        else -> getKoin().logger.log(Level.ERROR, localizedMessage)
                    }
                })
                urbanDictModelForSlashCommand?.let {
                    if (it.list.isNotEmpty()) {
                        val urbanDictItem = it.list.first()
                        val definitionCount = urbanDictItem.definition.count()
                        val exampleCount = urbanDictItem.example.count()
                        val authorCount = urbanDictItem.author.count()
                        if (definitionCount + exampleCount + authorCount > 2000) {
                            this.respond {
                                content = "Can not send the response since it is too long, " +
                                        "Here is the link to that page for you instead.\n" +
                                        urbanDictItem.permalink
                            }
                        } else {
                            this.respond {
                                embed {
                                    setupUrbanDictEmbed(arguments, urbanDictItem)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun EmbedBuilder.setupUrbanDictEmbed(arguments: UrbanDictArguments, urbanDictItem: UrbanDictItem) {
        title = "Urban Dictionary"
        field {
            name = arguments.search
            value = urbanDictItem.definition
        }
        field {
            name = "Example"
            value = urbanDictItem.example
        }
        field {
            name = "Added by"
            value = urbanDictItem.author
            inline = true
        }
        footer = kordClient.getEmbedFooter()
        timestamp = Clock.System.now()
    }
}
