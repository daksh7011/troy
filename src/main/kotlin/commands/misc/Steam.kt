package commands.misc

import com.kotlindiscord.kord.extensions.DISCORD_FUCHSIA
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescedString
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.embed
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import models.SteamGameModel
import models.SteamSearchModel
import org.koin.core.component.inject
import org.koin.core.logger.Level
import utils.Extensions
import utils.Extensions.getEmbedFooter
import utils.Extensions.requestAndCatch

class Steam : Extension() {

    private val kordClient: Kord by inject()

    private val jsonSerializer = Json { ignoreUnknownKeys = true }

    private val httpClient = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override val name: String
        get() = "steam"

    class SteamSearchArguments : Arguments() {
        val gameName by coalescedString("game-name", "Which game do you want to search for?")
    }

    override suspend fun setup() {
        chatCommand(::SteamSearchArguments) {
            name = "steam"
            description = "Searches Steam for your query."
            action {
                var steamSearchModel: SteamSearchModel? = null
                var steamGameJsonObject: JsonObject? = null
                val url = "https://store.steampowered.com/api/storesearch?cc=us&l=en&term=${arguments.gameName}"
                httpClient.requestAndCatch(
                    {
                        steamSearchModel = get<SteamSearchModel>(url)
                    }, {
                        when (response.status) {
                            HttpStatusCode.BadRequest -> {
                                getKoin().logger.log(Level.ERROR, localizedMessage)
                            }
                            HttpStatusCode.NotFound -> {
                                this@action.message.respond("Can't the data for requested game!")
                            }
                            else -> getKoin().logger.log(Level.ERROR, localizedMessage)
                        }
                    })
                if (steamSearchModel?.gameItems == null ||
                    steamSearchModel?.gameItems?.isEmpty() != false
                ) {
                    message.respond { content = "Can't the data for requested game!" }
                    return@action
                }
                steamSearchModel?.gameItems?.get(0)?.let {
                    if (it.id == null) {
                        message.respond { content = "Can't the data for requested game!" }
                        return@action
                    }
                    httpClient.requestAndCatch(
                        {
                            val steamGameUrl = "https://store.steampowered.com/api/appdetails?appids=${it.id}"
                            steamGameJsonObject = this.get(steamGameUrl)
                        },
                        {
                            when (response.status) {
                                HttpStatusCode.BadRequest -> {
                                    getKoin().logger.log(Level.ERROR, localizedMessage)
                                }
                                HttpStatusCode.NotFound -> {
                                    this@action.message.respond("Can't the data for requested game!")
                                }
                                else -> getKoin().logger.log(Level.ERROR, localizedMessage)
                            }
                        }
                    )
                }
                val steamGameJsonString =
                    (steamGameJsonObject?.get(steamGameJsonObject?.entries?.first()?.key) as JsonObject)["data"]
                jsonSerializer.decodeFromString<SteamGameModel?>(steamGameJsonString.toString())?.let {
                    message.channel.createEmbed {
                        setupSteamEmbed(it, getGamePlatforms(it))
                    }
                }
            }
        }
        publicSlashCommand(::SteamSearchArguments) {
            name = "steam"
            description = "Searches Steam for your query."
            guild(Extensions.getTestGuildSnowflake())
            action {
                respond {
                    var steamSearchModelForSlashCommand: SteamSearchModel? = null
                    var steamGameJsonObject: JsonObject? = null
                    val url = "https://store.steampowered.com/api/storesearch?cc=us&l=en&term=${arguments.gameName}"
                    httpClient.requestAndCatch(
                        {
                            steamSearchModelForSlashCommand = get<SteamSearchModel>(url)
                        }, {
                            when (response.status) {
                                HttpStatusCode.BadRequest -> {
                                    getKoin().logger.log(Level.ERROR, localizedMessage)
                                }
                                HttpStatusCode.NotFound -> {
                                    this@action.respond { content = "Can't the data for requested game!" }
                                }
                                else -> getKoin().logger.log(Level.ERROR, localizedMessage)
                            }
                        })
                    if (steamSearchModelForSlashCommand?.gameItems == null ||
                        steamSearchModelForSlashCommand?.gameItems?.isEmpty() != false
                    ) {
                        respond { content = "Can't the data for requested game!" }
                        return@action
                    }
                    steamSearchModelForSlashCommand?.gameItems?.get(0)?.let {
                        if (it.id == null) {
                            respond { content = "Can't the data for requested game!" }
                            return@action
                        }
                        httpClient.requestAndCatch(
                            {
                                val steamGameUrl = "https://store.steampowered.com/api/appdetails?appids=${it.id}"
                                steamGameJsonObject = this.get(steamGameUrl)
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
                            }
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
            value = steamGameModel.recommendations.total.toString()
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
            value = steamGameModel.dlc?.size.toString()
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
