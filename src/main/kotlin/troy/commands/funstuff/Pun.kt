package troy.commands.funstuff

import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.withTimeoutOrNull
import troy.apiModels.PunsModel
import troy.utils.commonLogger
import troy.utils.httpClient
import troy.utils.requestAndCatch

class Pun : Extension() {

    companion object {
        private const val PUN_API_URL = "https://icanhazdadjoke.com/"
        private const val REQUEST_TIMEOUT_MS = 5000L
    }

    override val name: String
        get() = "pun"

    override suspend fun setup() {
        publicSlashCommand {
            name = "pun".toKey()
            description = "Sends a pun".toKey()
            action {
                // Use local variable instead of class property to avoid concurrency issues
                var punsModel: PunsModel? = null

                // Add timeout to HTTP request to prevent hanging
                val success = withTimeoutOrNull(REQUEST_TIMEOUT_MS) {
                    httpClient.requestAndCatch(
                        { punsModel = get(PUN_API_URL).body() },
                        { commonLogger.error { "Failed to fetch pun: $localizedMessage" } },
                    )
                    true
                } ?: false

                // Store joke in a local variable to avoid smart cast issues
                val joke = if (success) punsModel?.joke else null

                if (joke != null) {
                    respond { content = joke }
                } else {
                    respond { content = "Sorry, I couldn't fetch a pun at the moment. Please try again later." }
                }
            }
        }
    }
}
