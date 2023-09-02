package commands.funstuff

import apiModels.FactModel
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
import org.koin.core.component.inject
import org.koin.core.logger.Level
import utils.getEmbedFooter
import utils.httpClient
import utils.requestAndCatch

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
                    getKoin().logger.log(Level.ERROR, localizedMessage)
                })
            }
        }
    }
}
