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
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject
import troy.apiModels.SteamGameModel
import troy.apiModels.SteamSearchModel
import troy.utils.encodeQuery
import troy.utils.getEmbedFooter
import troy.utils.httpClient
import troy.utils.requestAndCatchResponse

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

    /**
     * Fetches the Steam search model for the given game name.
     *
     * @param gameName The name of the game to search for
     * @param respondWithError Function to respond with an error message
     * @return The Steam search model, or null if not found
     */
    private suspend fun fetchSteamSearchModel(
        gameName: String,
        respondWithError: suspend (String) -> Unit
    ): SteamSearchModel? {
        val url = "https://store.steampowered.com/api/storesearch?cc=us&l=en&term=${gameName.encodeQuery()}"

        return httpClient.requestAndCatchResponse(
            identifier = this@Steam.name,
            block = { get(url).body() },
            notFoundHandler = {
                respondWithError(dataNotFound)
                null
            },
            logPrefix = "Failed to fetch Steam search results"
        )
    }

    /**
     * Fetches the Steam game details for the given game ID.
     *
     * @param gameId The ID of the game to fetch details for
     * @param respondWithError Function to respond with an error message
     * @return The JSON object containing the game details, or null if not found
     */
    private suspend fun fetchSteamGameDetails(
        gameId: Int,
        respondWithError: suspend (String) -> Unit
    ): JsonObject? {
        val steamGameUrl = "https://store.steampowered.com/api/appdetails?appids=$gameId"

        return httpClient.requestAndCatchResponse(
            identifier = this@Steam.name,
            block = { get(steamGameUrl).body() },
            notFoundHandler = {
                respondWithError(dataNotFound)
                null
            },
            logPrefix = "Failed to fetch Steam game details"
        )
    }

    /**
     * Extracts the game data from the Steam game JSON object.
     *
     * @param steamGameJsonObject The JSON object containing the game details
     * @return The game data as a JsonElement, or null if not found
     */
    private fun extractGameData(steamGameJsonObject: JsonObject?): JsonElement? {
        return steamGameJsonObject?.let { jsonObj ->
            val key = jsonObj.entries.firstOrNull()?.key
            if (key != null) {
                (jsonObj[key] as? JsonObject)?.get("data")
            } else {
                null
            }
        }
    }

    override suspend fun setup() {
        publicSlashCommand(Steam::SteamSearchArguments) {
            name = "steam".toKey()
            description = "Searches Steam for your query.".toKey()
            action {
                // Function to respond with an error message
                val respondWithError: suspend (String) -> Unit = { errorMessage ->
                    respond { content = errorMessage }
                }

                // Fetch the Steam search model
                val steamSearchModel = fetchSteamSearchModel(arguments.gameName, respondWithError)

                // Check if the search model has any game items
                if (steamSearchModel?.gameItems == null || steamSearchModel.gameItems.isEmpty()) {
                    respondWithError(dataNotFound)
                    return@action
                }

                // Get the first game item
                val gameItem = steamSearchModel.gameItems[0]
                if (gameItem.id == null) {
                    respondWithError(dataNotFound)
                    return@action
                }

                // Fetch the game details
                val steamGameJsonObject = fetchSteamGameDetails(gameItem.id, respondWithError)

                // Extract the game data
                val gameData = extractGameData(steamGameJsonObject)

                // If game data is found, decode it and respond with the embed
                if (gameData != null) {
                    jsonSerializer.decodeFromString<SteamGameModel?>(gameData.toString())
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
