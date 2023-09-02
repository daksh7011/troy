package commands.funstuff

import apiModels.PunsModel
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.koin.core.logger.Level
import utils.httpClient
import utils.requestAndCatch

class Pun : Extension() {

    private var punsModel: PunsModel? = null

    override val name: String
        get() = "pun"

    override suspend fun setup() {
        publicSlashCommand {
            name = "pun"
            description = "Sends a pun"
            action {
                val url = "https://icanhazdadjoke.com/"
                httpClient.requestAndCatch(
                    { punsModel = get(url).body() },
                    { getKoin().logger.log(Level.ERROR, localizedMessage) },
                )
                if (punsModel != null) {
                    respond { content = punsModel?.joke }
                }
            }
        }
    }
}
