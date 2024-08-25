package troy.commands.funstuff

import dev.kord.core.Kord
import dev.kord.rest.builder.message.embed
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import troy.apiModels.FactModel
import troy.utils.commonLogger
import troy.utils.getEmbedFooter
import troy.utils.httpClient
import troy.utils.requestAndCatch

class Fact : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "fact"

    override suspend fun setup() {
        publicSlashCommand {
            name = "fact"
            description = "Finds some useless facts."
            action {
                val url = "https://uselessfacts.jsph.pl/random.json?language=en"
                httpClient.requestAndCatch({
                    get(url).body<FactModel>().let {
                        respond {
                            embed {
                                title = "A useless fact for you"
                                description = it.text
                                field {
                                    name = "Source"
                                    value = it.permalink
                                    inline = false
                                }
                                footer = kordClient.getEmbedFooter()
                                timestamp = Clock.System.now()
                            }
                        }
                    }
                }, {
                    commonLogger.error { localizedMessage }
                })
            }
        }
    }
}
