package troy.commands.funstuff

import dev.kord.core.Kord
import dev.kord.rest.builder.message.embed
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import troy.apiModels.FactModel
import troy.utils.commonLogger
import troy.utils.getEmbedFooter
import troy.utils.httpClient
import troy.utils.requestAndCatchResponse

class Fact : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "fact"

    override suspend fun setup() {
        publicSlashCommand {
            name = "fact".toKey()
            description = "Finds some useless facts.".toKey()
            action {
                var factModel: FactModel? = null

                // Add timeout to HTTP request to prevent hanging
                val success = withTimeoutOrNull(REQUEST_TIMEOUT_MS) {
                    val result = httpClient.requestAndCatchResponse(
                        identifier = this@Fact.name,
                        block = {
                            factModel = get(FACT_API_URL).body()
                            true
                        },
                        logPrefix = "Failed to fetch fact"
                    )
                    result ?: false
                } ?: run {
                    commonLogger.error { "Timeout occurred while fetching fact" }
                    false
                }

                if (success) {
                    // Create a local non-null reference to factModel
                    factModel?.let { model ->
                        respond {
                            embed {
                                title = TITLE
                                description = model.text
                                field {
                                    name = SOURCE_FIELD_NAME
                                    value = model.permalink
                                    inline = false
                                }
                                footer = kordClient.getEmbedFooter()
                                timestamp = Clock.System.now()
                            }
                        }
                    }
                } else {
                    respond { content = ERROR_MESSAGE }
                }
            }
        }
    }

    companion object {
        private const val FACT_API_URL = "https://uselessfacts.jsph.pl/random.json?language=en"
        private const val REQUEST_TIMEOUT_MS = 5000L
        private const val TITLE = "A useless fact for you"
        private const val SOURCE_FIELD_NAME = "Source"
        private const val ERROR_MESSAGE = "Sorry, I couldn't fetch a fact at the moment. Please try again later."
    }
}
