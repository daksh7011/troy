package troy.commands.funstuff

import dev.kord.core.Kord
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.embed
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import troy.apiModels.UrbanDictItem
import troy.apiModels.UrbanDictModel
import troy.utils.*

class UrbanDictionary : Extension() {

    private val kordClient: Kord by inject()

    private val urbanApiUrl = "https://api.urbandictionary.com/v0/define?term"

    override val name: String
        get() = "urban"

    class UrbanDictArguments : Arguments() {
        val search by string {
            name = "query".toKey()
            description = "What do you want to search at UrbanDictionary?".toKey()
        }
    }

    override suspend fun setup() {
        publicSlashCommand(UrbanDictionary::UrbanDictArguments) {
            name = "urban".toKey()
            description = "Returns a definition from Urban Dictionary".toKey()
            action {
                var urbanDictModel: UrbanDictModel? = null
                val search = arguments.search.encodeQuery()
                httpClient.requestAndCatch({
                    urbanDictModel = this.get("$urbanApiUrl=$search").body()
                }, {
                    if (response.status == HttpStatusCode.NotFound) {
                        this@action.respond {
                            content = "Can't the data for ${arguments.search}"
                        }
                    } else {
                        commonLogger.error { localizedMessage }
                    }
                })
                urbanDictModel?.let {
                    if (it.list.isNotEmpty()) {
                        val urbanDictItem = it.list.first()
                        val definitionCount = urbanDictItem.definition.count()
                        val exampleCount = urbanDictItem.example.count()
                        val authorCount = urbanDictItem.author.count()
                        if (definitionCount + exampleCount + authorCount > MAX_CHARS) {
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

    private companion object {
        private const val MAX_CHARS = 2000
    }
}
