package commands.`fun`

import apiModels.PunsModel
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import org.koin.core.logger.Level
import utils.requestAndCatch

class Pun : Extension() {

    private val httpClient = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }
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
                    { punsModel = get(url) },
                    { getKoin().logger.log(Level.ERROR, localizedMessage) }
                )
                if (punsModel != null) {
                    respond { content = punsModel?.joke }
                }
            }
        }
    }
}
