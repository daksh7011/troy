package commands.funstuff

import apiModels.DoggoModel
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingString
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.Kord
import dev.kord.rest.builder.message.create.embed
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import org.koin.core.logger.Level
import utils.getEmbedFooter
import utils.requestAndCatch

class Doggo : Extension() {

    private val kordClient: Kord by inject()
    private var doggoModel: DoggoModel? = null
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    override val name: String
        get() = "doggo"

    class DoggoArguments : Arguments() {
        val breed by defaultingString {
            name = "breed"
            description = "Which breed of good boi you want to see?"
            defaultValue = "random"
        }
    }

    override suspend fun setup() {
        publicSlashCommand(::DoggoArguments) {
            name = "doggo"
            description = "Finds some cute doggo images."
            action {
                val url = if (arguments.breed == "random") {
                    "https://dog.ceo/api/breeds/image/random"
                } else {
                    "https://dog.ceo/api/breed/${arguments.breed}/images/random"
                }
                httpClient.requestAndCatch(
                    {
                        doggoModel = get(url).body()
                    },
                    {
                        when (response.status) {
                            HttpStatusCode.NotFound -> {
                                this@action.respond { content = "Can't find any good boi photo." }
                            }

                            else -> getKoin().logger.log(Level.ERROR, localizedMessage)
                        }
                    },
                )
                if (doggoModel != null) {
                    respond {
                        embed {
                            title = "Woof Woof \uD83D\uDC36"
                            description = "A cute doggo image for you."
                            image = doggoModel?.message
                            footer = kordClient.getEmbedFooter()
                            timestamp = Clock.System.now()
                        }
                    }
                }
            }
        }
    }
}
