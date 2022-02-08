package commands.`fun`

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingString
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.rest.builder.message.create.embed
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.Clock
import apiModels.DoggoModel
import org.koin.core.component.inject
import org.koin.core.logger.Level
import utils.requestAndCatch
import utils.getEmbedFooter

class Doggo : Extension() {

    private val kordClient: Kord by inject()
    private var doggoModel: DoggoModel? = null
    private val httpClient = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    override val name: String
        get() = "doggo"

    class DoggoArguments : Arguments() {
        val breed by defaultingString("breed", "Which breed of good boi you want to see?", "random")
    }

    class DoggoSlashArguments : Arguments() {
        val breedName by defaultingString("breed", "Which breed of good boi you want to see?", "random")
    }

    override suspend fun setup() {
        chatCommand(::DoggoArguments) {
            name = "doggo"
            description = "Finds some cute doggo images."
            action {
                val url = if (arguments.breed == "random") "https://dog.ceo/api/breeds/image/random"
                else "https://dog.ceo/api/breed/${arguments.breed}/images/random"
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
            }
        }

        publicSlashCommand(::DoggoSlashArguments) {
            name = "doggo"
            description = "Finds some cute doggo images."
            action {
                val url = if (arguments.breedName == "random") "https://dog.ceo/api/breeds/image/random"
                else "https://dog.ceo/api/breed/${arguments.breedName}/images/random"
                httpClient.requestAndCatch(
                    {
                        doggoModel = get<DoggoModel>(url)
                    },
                    {
                        when (response.status) {
                            HttpStatusCode.NotFound -> {
                                this@action.respond { content = "Can't find any good boi photo." }
                            }
                            else -> getKoin().logger.log(Level.ERROR, localizedMessage)
                        }
                    }
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
