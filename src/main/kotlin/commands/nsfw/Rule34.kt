package commands.nsfw

import apiModels.Rule34Model
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.Kord
import dev.kord.rest.builder.message.create.embed
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.koin.core.component.inject
import org.koin.core.logger.Level
import utils.getEmbedFooter

class Rule34 : Extension() {

    override val name: String = "rule34"

    private val kordClient: Kord by inject()

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    class Rule34Arguments : Arguments() {
        val search by string {
            name = "search"
            description = "What do you want to see?"
        }
    }

    override suspend fun setup() {
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
                        httpClient.get(url).body<List<Rule34Model>>().let {
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
