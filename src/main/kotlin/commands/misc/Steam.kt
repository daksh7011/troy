package commands.misc

import apiModels.SteamGameModel
import apiModels.SteamSearchModel
import com.kotlindiscord.kord.extensions.DISCORD_FUCHSIA
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescingString
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
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject
import org.koin.core.logger.Level
import utils.encodeQuery
import utils.getEmbedFooter
import utils.httpClient
import utils.requestAndCatch

class Steam : Extension() {

    private val kordClient: Kord by inject()
    private val jsonSerializer = Json { ignoreUnknownKeys = true }

    override val name: String
        get() = "steam"

    class SteamSearchArguments : Arguments() {
        val gameName by coalescingString {
            name = "game-name"
            description = "Which game do you want to search for?"
        }
    }

    override suspend fun setup() {
        publicSlashCommand(::SteamSearchArguments) {
            name = "steam"
            description = "Searches Steam for your query."
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
                            HttpStatusCode.BadRequest -> {
                                getKoin().logger.log(Level.ERROR, localizedMessage)
                            }

                            HttpStatusCode.NotFound -> {
                                this@action.respond { content = "Can't the data for requested game!" }
                            }

                            else -> getKoin().logger.log(Level.ERROR, localizedMessage)
                        }
                    },
                )
                if (steamSearchModel?.gameItems == null ||
                    steamSearchModel?.gameItems?.isEmpty() != false
                ) {
                    respond { content = "Can't the data for requested game!" }
                    return@action
                }
                steamSearchModel?.gameItems?.get(0)?.let {
                    if (it.id == null) {
                        respond { content = "Can't the data for requested game!" }
                        return@action
                    }
                    httpClient.requestAndCatch(
                        {
                            val steamGameUrl = "https://store.steampowered.com/api/appdetails?appids=${it.id}"
                            steamGameJsonObject = get(steamGameUrl).body()
                        },
                        {
                            when (response.status) {
                                HttpStatusCode.BadRequest -> {
                                    getKoin().logger.log(Level.ERROR, localizedMessage)
                                }

                                HttpStatusCode.NotFound -> {
                                    this@action.respond { content = "Can't the data for requested game!" }
                                }

                                else -> getKoin().logger.log(Level.ERROR, localizedMessage)
                            }
                        },
                    )
                }
                val steamGameJsonString =
                    (steamGameJsonObject?.get(steamGameJsonObject?.entries?.first()?.key) as JsonObject)["data"]
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
