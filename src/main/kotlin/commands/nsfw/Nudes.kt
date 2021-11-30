package commands.nsfw

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingString
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock
import models.NoodsModel
import net.dean.jraw.models.SubredditSort
import org.koin.core.component.inject
import utils.DataProvider
import utils.Extensions.getEmbedFooter
import utils.RedditClient
import kotlin.math.floor

class Nudes : Extension() {

    private val kordClient: Kord by inject()

    private var redditClient = RedditClient.getClient()

    private val nudesCatalog = DataProvider.getNoodsData()

    override val name: String get() = "nudes"

    class NudesArguments : Arguments() {
        val category by defaultingString("category", "What will tickle your pickle?", "random")
    }

    override suspend fun setup() {
        chatCommand(::NudesArguments) {
            name = "nudes"
            description = "Finds some spicy nudes."
            aliases = arrayOf("noods", "gulabi", "nsfw")
            action {
                if (kordClient.getChannel(channel.id)?.data?.nsfw?.orElse(false) == false) {
                    this@action.message.respond("Eh, Are you lost boi?")
                } else {
                    val nudeUrl = getNudeUrl(arguments.category)
                    if (nudeUrl.isNullOrBlank()) {
                        val randomCategoryList = nudesCatalog.random().value
                        message.channel.createMessage(
                            "Can not find nude for given category. Try different category.\n" +
                                    "Maybe try anything form this: ${randomCategoryList.take(2).joinToString()}"
                        )
                    } else {
                        message.channel.createEmbed {
                            title = "Go. Enjoy those 10 seconds."
                            description = getCategoriesFromCatalog(arguments.category)[0].fullName
                            image = nudeUrl
                            footer = message.getEmbedFooter()
                            timestamp = Clock.System.now()
                        }
                    }
                }
            }
        }

        publicSlashCommand(::NudesArguments) {
            name = "nudes"
            description = "Finds some spicy noods."
            action {
                if (kordClient.getChannel(channel.id)?.data?.nsfw?.orElse(false) == false) {
                    this@action.respond {
                        content = "Eh, Are you lost boi?"
                    }
                } else {
                    respond {
                        val nudeUrl = getNudeUrl(arguments.category)
                        if (nudeUrl.isNullOrBlank()) {
                            val randomCategoryList = nudesCatalog.random().value
                            content = "Can not find nude for given category. Try different category.\n" +
                                    "Maybe try anything form this: ${randomCategoryList.take(2).joinToString()}"
                        } else {
                            embed {
                                title = "Go. Enjoy those 10 seconds."
                                description = getCategoriesFromCatalog(arguments.category)[0].fullName
                                image = nudeUrl
                                footer = kordClient.getEmbedFooter()
                                timestamp = Clock.System.now()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getNudeUrl(userArgument: String): String? {
        val categoryToFetch = getCategoriesFromCatalog(userArgument)
        val category = getRandomCategory(categoryToFetch)
        if (category.isNullOrBlank()) return null
        val posts = getPostsFromReddit(category)
        val data = posts.next()
        var nudeUrl = data.children[floor(Math.random() * data.children.size).toInt()].url
        while (!(nudeUrl.contains(".jpg") or nudeUrl.contains(".png"))) {
            nudeUrl = data.children[floor(Math.random() * data.children.size).toInt()].url
        }
        return nudeUrl
    }

    private fun getCategoriesFromCatalog(categoryName: String?): List<NoodsModel> {
        return if (categoryName.isNullOrBlank()) listOf()
        else nudesCatalog.filter { it.categoryName.contains(categoryName) }
    }

    private fun getRandomCategory(categoryToFetch: List<NoodsModel>): String? {
        return if (categoryToFetch.isNotEmpty())
            categoryToFetch[0].value[floor(Math.random() * categoryToFetch.size).toInt()]
        else null
    }

    private fun getPostsFromReddit(category: String) =
        redditClient.subreddit(category).posts().sorting(SubredditSort.HOT).limit(floor(Math.random() * 69).toInt())
            .build()
}
