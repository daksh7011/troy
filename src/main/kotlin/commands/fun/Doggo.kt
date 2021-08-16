package commands.`fun`

import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingString
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import com.kotlindiscord.kord.extensions.utils.respond
import core.TroyExtension
import dev.kord.core.behavior.channel.createEmbed
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.Clock
import models.DoggoModel
import org.koin.core.logger.Level
import utils.Extensions.getEmbedFooter
import utils.Extensions.requestAndCatch

class Doggo : TroyExtension() {

    override val name: String
        get() = "doggo"

    class DoggoArguments : Arguments() {
        val breed by defaultingString("breed", "Which breed of good boi you want to see?", "random")
    }

    override suspend fun setup() {
        troyCommand(::DoggoArguments) {
            name = "doggo"
            description = "Finds some cute doggo images."
            action {
                var doggoModel: DoggoModel? = null
                val url = if (arguments.breed == "random") "https://dog.ceo/api/breeds/image/random"
                else "https://dog.ceo/api/breed/${arguments.breed}/images/random"

                val httpClient = HttpClient {
                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
                }

                httpClient.requestAndCatch(
                    {
                        doggoModel = get<DoggoModel>(url)
                    },
                    {
                        when (response.status) {
                            HttpStatusCode.NotFound -> {
                                this@action.message.respond("Can't find any good boi photo.")
                            }
                            else -> getKoin().logger.log(Level.ERROR, localizedMessage)
                        }
                    }
                )

                message.channel.createEmbed {
                    title = "Woof Woof \uD83D\uDC36"
                    description = "A cute doggo image for you."
                    image = doggoModel?.message
                    footer = message.getEmbedFooter()
                    timestamp = Clock.System.now()
                }
                httpClient.close()
            }
        }
    }
}
