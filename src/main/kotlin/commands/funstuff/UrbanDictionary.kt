package commands.funstuff

import apiModels.UrbanDictItem
import apiModels.UrbanDictModel
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.Kord
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.embed
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.koin.core.component.inject
import org.koin.core.logger.Level
import utils.getEmbedFooter
import utils.requestAndCatch

class UrbanDictionary : Extension() {

    private val kordClient: Kord by inject()
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    private val urbanApiUrl = "https://api.urbandictionary.com/v0/define?term="

    override val name: String
        get() = "urban"

    class UrbanDictArguments : Arguments() {
        val search by string {
            name = "query"
            description = "What do you want to search at UrbanDictionary?"
        }
    }

    override suspend fun setup() {
        publicSlashCommand(::UrbanDictArguments) {
            name = "urban"
            description = "Returns a definition from Urban Dictionary"
            action {
                var urbanDictModel: UrbanDictModel? = null
                httpClient.requestAndCatch({
                    urbanDictModel = this.get(urbanApiUrl + arguments.search).body()
                }, {
                    when (response.status) {
                        HttpStatusCode.NotFound -> {
                            this@action.respond { content = "Can't the data for ${arguments.search}" }
                        }

                        else -> getKoin().logger.log(Level.ERROR, localizedMessage)
                    }
                })
                urbanDictModel?.let {
                    if (it.list.isNotEmpty()) {
                        val urbanDictItem = it.list.first()
                        val definitionCount = urbanDictItem.definition.count()
                        val exampleCount = urbanDictItem.example.count()
                        val authorCount = urbanDictItem.author.count()
                        if (definitionCount + exampleCount + authorCount > 2000) {
                            this.respond {
                                content = "Can not send the response since it is too long, " +
                                    "Here is the link to that page for you instead.\n" +
                                    urbanDictItem.permalink
                            }
                        } else {
                            this.respond {
                                embed {
                                    setupUrbanDictEmbed(arguments, urbanDictItem)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun EmbedBuilder.setupUrbanDictEmbed(arguments: UrbanDictArguments, urbanDictItem: UrbanDictItem) {
        title = "Urban Dictionary"
        field {
            name = arguments.search
            value = urbanDictItem.definition
        }
        field {
            name = "Example"
            value = urbanDictItem.example
        }
        field {
            name = "Added by"
            value = urbanDictItem.author
            inline = true
        }
        footer = kordClient.getEmbedFooter()
        timestamp = Clock.System.now()
    }
}
