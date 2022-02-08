package commands.nsfw

import apiModels.Rule34Model
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.rest.builder.message.create.embed
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import org.koin.core.logger.Level
import utils.getEmbedFooter

class Rule34 : Extension() {

    override val name: String = "rule34"

    private val kordClient: Kord by inject()

    private val httpClient = HttpClient {
        install(JsonFeature) {
            val json = kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
            serializer = KotlinxSerializer(json)
        }
    }

    class Rule34Arguments : Arguments() {
        val search by string("search", "What do you want to see?")
    }

    override suspend fun setup() {
        chatCommand(::Rule34Arguments) {
            name = "rule34"
            description = "Provides Rule34 content for given search query."
            action {
                if (kordClient.getChannel(channel.id)?.data?.nsfw?.orElse(false) == false) {
                    message.channel.createMessage("Are you lost boi?")
                } else {
                    val url =
                        "https://rule34.xxx/index.php?page=dapi&s=post&q=index&limit=1000&json=1&tags=${arguments.search}"
                    try {
                        httpClient.get<List<Rule34Model>>(url).let {
                            message.channel.createEmbed {
                                title = "Here is a fine piece of art"
                                image = it.random().fileUrl
                                footer = kordClient.getEmbedFooter()
                                timestamp = Clock.System.now()
                            }
                        }
                    } catch (exception: Exception) {
                        message.channel.createMessage(
                            "Could not find the results for given query, Please try again!"
                        )
                        getKoin().logger.log(Level.ERROR, exception.localizedMessage)
                    }
                }
            }
        }
        publicSlashCommand(::Rule34Arguments) {
            name = "rule34"
            description = "Provides Rule34 content for given search query."
            action {
                if (kordClient.getChannel(channel.id)?.data?.nsfw?.orElse(false) == false) {
                    respond {
                        content = "Are you lost boi?"
                    }
                } else {
                    val url =
                        "https://rule34.xxx/index.php?page=dapi&s=post&q=index&limit=1000&json=1&tags=${arguments.search}"
                    try {
                        httpClient.get<List<Rule34Model>>(url).let {
                            respond {
                                embed {
                                    title = "Here is a fine piece of art"
                                    image = it.random().fileUrl
                                    footer = kordClient.getEmbedFooter()
                                    timestamp = Clock.System.now()
                                }
                            }
                        }
                    } catch (exception: Exception) {
                        respond {
                            content = "Could not find the results for given query, Please try again!"
                        }
                        getKoin().logger.log(Level.ERROR, exception.localizedMessage)
                    }
                }
            }
        }
    }
}
