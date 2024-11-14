package troy.commands.misc

import dev.kord.core.Kord
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_FUCHSIA
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.coalescingString
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject
import troy.apiModels.SteamGameModel
import troy.apiModels.SteamSearchModel
import troy.utils.commonLogger
import troy.utils.encodeQuery
import troy.utils.getEmbedFooter
import troy.utils.httpClient
import troy.utils.requestAndCatch

class Steam : Extension() {

    private val kordClient: Kord by inject()
    private val jsonSerializer = Json { ignoreUnknownKeys = true }
    private val dataNotFound = "Can't the data for requested game!"

    override val name: String
        get() = "steam"

    class SteamSearchArguments : Arguments() {
        val gameName by coalescingString {
            name = "game-name".toKey()
            description = "Which game do you want to search for?".toKey()
        }
    }

    override suspend fun setup() {
        publicSlashCommand(Steam::SteamSearchArguments) {
            name = "steam".toKey()
            description = "Searches Steam for your query.".toKey()
            action {
                var steamSearchModel: SteamSearchModel? = null
                var steamGameJsonObject: JsonObject? = null
                val url = "https://store.steampowered.com/api/storesearch?cc=us&l=en&term=${arguments.gameName.encodeQuery()}"
                httpClient.requestAndCatch(
                    {
                        steamSearchModel = get(url).body()
                    },
                    {
                        when (response.status) {
                            HttpStatusCode.BadRequest -> commonLogger.error { localizedMessage }
                            HttpStatusCode.NotFound -> this@action.respond { content = dataNotFound }
                            else -> commonLogger.error { localizedMessage }
                        }
                    },
                )
                if (steamSearchModel?.gameItems == null ||
                    steamSearchModel?.gameItems?.isEmpty() != false
                ) {
                    respond { content = dataNotFound }
                    return@action
                }
                steamSearchModel?.gameItems?.get(0)?.let {
                    if (it.id == null) {
                        respond { content = dataNotFound }
                        return@action
                    }
                    httpClient.requestAndCatch(
                        {
                            val steamGameUrl = "https://store.steampowered.com/api/appdetails?appids=${it.id}"
                            steamGameJsonObject = get(steamGameUrl).body()
                        },
                        {
                            when (response.status) {
                                HttpStatusCode.BadRequest -> commonLogger.error { localizedMessage }
                                HttpStatusCode.NotFound -> this@action.respond { content = dataNotFound }
                                else -> commonLogger.error { localizedMessage }
                            }
                        },
                    )
                }
                val steamGameJsonString =
                    (steamGameJsonObject?.get(steamGameJsonObject?.entries?.first()?.key) as JsonObject)["troy/data/data"]
                if (steamGameJsonString != null) {
                    jsonSerializer.decodeFromString<SteamGameModel?>(steamGameJsonString.toString())
                        ?.let { steamGameModel ->
                            respond {
                                embed {
                                    setupSteamEmbed(steamGameModel, getGamePlatforms(steamGameModel))
                                }
                            }
                        }
                }
            }
        }
    }

    private fun getGamePlatforms(steamGameModel: SteamGameModel): MutableList<String> {
        val platformsListForSlashCommand = mutableListOf<String>()
        if (steamGameModel.platforms.windows) platformsListForSlashCommand.add("Windows")
        if (steamGameModel.platforms.mac) platformsListForSlashCommand.add("Mac")
        if (steamGameModel.platforms.linux) platformsListForSlashCommand.add("Linux")
        return platformsListForSlashCommand
    }

    private suspend fun EmbedBuilder.setupSteamEmbed(
        steamGameModel: SteamGameModel,
        platformsListForSlashCommand: MutableList<String>
    ) {
        author {
            name = "Steam"
            icon = "https://i.imgur.com/xxr2UBZ.png"
            this.url = "https://store.steampowered.com/"
        }
        color = DISCORD_FUCHSIA
        title = steamGameModel.name
        this.url = "https://store.steampowered.com/app/${steamGameModel.steamAppid}"
        thumbnail { this.url = steamGameModel.headerImage }
        footer = kordClient.getEmbedFooter()
        timestamp = Clock.System.now()
        field {
            name = "❯ Price"
            value = steamGameModel.priceOverview.finalFormatted
            inline = true
        }
        field {
            name = "❯ Recommendations"
            value = "%,d".format(steamGameModel.recommendations.total)
            inline = true
        }
        field {
            name = "❯ Platforms"
            value = platformsListForSlashCommand.joinToString()
            inline = true
        }
        field {
            name = "❯ Release Date"
            value = steamGameModel.releaseDate.date
            inline = true
        }
        field {
            name = "❯ DLC Count"
            value = if (steamGameModel.dlc.isNullOrEmpty()) "Info not found" else steamGameModel.dlc.size.toString()
            inline = true
        }
        field {
            name = "❯ Developers"
            value = steamGameModel.developers.joinToString()
            inline = true
        }
        field {
            name = "❯ Publishers"
            value = steamGameModel.publishers.joinToString()
            inline = true
        }
    }
}
