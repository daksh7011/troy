package commands.`fun`

import apiModels.FactModel
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.Kord
import dev.kord.rest.builder.message.create.embed
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import org.koin.core.logger.Level
import utils.getEmbedFooter
import utils.requestAndCatch

class Fact : Extension() {

    private val kordClient: Kord by inject()
    private val httpClient = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }
    override val name: String
        get() = "fact"

    override suspend fun setup() {
        publicSlashCommand(Doggo::DoggoSlashArguments) {
            name = "fact"
            description = "Finds some useless facts."
            action {
                val url = "https://uselessfacts.jsph.pl/random.json?language=en"
                httpClient.requestAndCatch({
                    get<FactModel>(url).let {
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
