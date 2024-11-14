package troy.commands.funstuff

import dev.kord.core.Kord
import dev.kord.rest.builder.message.embed
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.defaultingString
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import troy.apiModels.DoggoModel
import troy.utils.commonLogger
import troy.utils.getEmbedFooter
import troy.utils.httpClient
import troy.utils.requestAndCatch

class Doggo : Extension() {

    private val kordClient: Kord by inject()
    private var doggoModel: DoggoModel? = null

    override val name: String
        get() = "doggo"

    class DoggoArguments : Arguments() {
        val breed by defaultingString {
            name = "breed".toKey()
            description = "Which breed of good boi you want to see?".toKey()
            defaultValue = "random"
        }
    }

    override suspend fun setup() {
        publicSlashCommand(Doggo::DoggoArguments) {
            name = "doggo".toKey()
            description = "Finds some cute doggo images.".toKey()
            action {
                val url = if (arguments.breed == "random") {
                    "https://dog.ceo/api/breeds/image/random"
                } else {
                    "https://dog.ceo/api/breed/${arguments.breed.replace(" ", "")}/images/random"
                }
                httpClient.requestAndCatch(
                    {
                        doggoModel = get(url).body()
                    },
                    {
                        if (response.status == HttpStatusCode.NotFound) {
                            this@action.respond { content = "Can't find any good boi photo." }
                        } else {
                            commonLogger.error { localizedMessage }
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
