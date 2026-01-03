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
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.component.inject
import troy.apiModels.DoggoModel
import troy.utils.commonLogger
import troy.utils.getEmbedFooter
import troy.utils.httpClient
import troy.utils.requestAndCatchResponse
import kotlin.time.Clock

class Doggo : Extension() {

    private val kordClient: Kord by inject()

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
                // Use local variable instead of class property to avoid concurrency issues
                var doggoModel: DoggoModel? = null

                val url = if (arguments.breed == "random") {
                    RANDOM_DOG_URL
                } else {
                    "$BREED_BASE_URL${arguments.breed.replace(" ", "")}/images/random"
                }

                // Add timeout to HTTP request to prevent hanging
                val success = withTimeoutOrNull(REQUEST_TIMEOUT_MS) {
                    val result = httpClient.requestAndCatchResponse(
                        identifier = this@Doggo.name,
                        block = {
                            doggoModel = get(url).body()
                            true
                        },
                        notFoundHandler = {
                            this@action.respond { content = NO_PHOTO_MESSAGE }
                            false
                        },
                        logPrefix = "Failed to fetch doggo image"
                    )
                    result ?: false
                } ?: run {
                    commonLogger.error { "Timeout occurred while fetching doggo image for breed: ${arguments.breed}" }
                    false
                }

                if (success && doggoModel != null) {
                    respond {
                        embed {
                            title = TITLE
                            description = DESCRIPTION
                            image = doggoModel?.message
                            footer = kordClient.getEmbedFooter()
                            timestamp = Clock.System.now()
                        }
                    }
                } else if (!success) {
                    respond { content = ERROR_MESSAGE }
                }
            }
        }
    }

    companion object {
        private const val RANDOM_DOG_URL = "https://dog.ceo/api/breeds/image/random"
        private const val BREED_BASE_URL = "https://dog.ceo/api/breed/"
        private const val REQUEST_TIMEOUT_MS = 5000L
        private const val TITLE = "Woof Woof \uD83D\uDC36"
        private const val DESCRIPTION = "A cute doggo image for you."
        private const val NO_PHOTO_MESSAGE = "Can't find any good boi photo."
        private const val ERROR_MESSAGE = "Sorry, I couldn't fetch a doggo image at the moment. Please try again later."
    }
}
