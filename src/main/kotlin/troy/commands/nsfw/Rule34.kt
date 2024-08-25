package troy.commands.nsfw

import dev.kord.core.Kord
import dev.kord.rest.builder.message.embed
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import troy.apiModels.Rule34Model
import troy.utils.commonLogger
import troy.utils.getEmbedFooter
import troy.utils.httpClient

class Rule34 : Extension() {

    override val name: String = "rule34"

    private val kordClient: Kord by inject()

    class Rule34Arguments : Arguments() {
        val search by string {
            name = "search"
            description = "What do you want to see?"
        }
    }

    override suspend fun setup() {
        publicSlashCommand(Rule34::Rule34Arguments) {
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
                        commonLogger.error { exception.localizedMessage }
                    }
                }
            }
        }
    }
}
