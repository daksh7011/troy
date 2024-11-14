package troy.commands.nsfw

import dev.kord.core.Kord
import dev.kord.rest.builder.message.embed
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.defaultingString
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import kotlinx.datetime.Clock
import net.dean.jraw.models.SubredditSort
import org.koin.core.component.inject
import troy.apiModels.NoodsModel
import troy.utils.DataProvider
import troy.utils.RedditClient
import troy.utils.getEmbedFooter
import kotlin.math.floor

class Nudes : Extension() {

    private val kordClient: Kord by inject()
    private var redditClient = RedditClient.getClient()
    private val nudesCatalog = DataProvider.getNoodsData()

    override val name: String get() = "nudes"

    class NudesArguments : Arguments() {
        val category by defaultingString {
            name = "category".toKey()
            description = "What will tickle your pickle?".toKey()
            defaultValue = "random"
        }
    }

    override suspend fun setup() {
        publicSlashCommand(Nudes::NudesArguments) {
            name = "nudes".toKey()
            description = "Finds some spicy noods.".toKey()
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
        return if (categoryName.isNullOrBlank()) {
            listOf()
        } else {
            nudesCatalog.filter { it.categoryName.contains(categoryName) }
        }
    }

    private fun getRandomCategory(categoryToFetch: List<NoodsModel>): String? {
        return if (categoryToFetch.isNotEmpty()) {
            categoryToFetch[0].value[floor(Math.random() * categoryToFetch.size).toInt()]
        } else {
            null
        }
    }

    private fun getPostsFromReddit(category: String) =
        redditClient.subreddit(category).posts().sorting(SubredditSort.HOT).limit(floor(Math.random() * MAGIC_RANDOMIZER).toInt()).build()

    private companion object {
        private const val MAGIC_RANDOMIZER = 69
    }
}
