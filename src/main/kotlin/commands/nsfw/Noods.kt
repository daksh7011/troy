package commands.nsfw

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingString
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orElse
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock
import net.dean.jraw.models.SubredditSort
import org.koin.core.component.inject
import utils.DataProvider
import utils.Extensions.getEmbedFooter
import utils.Extensions.getTestGuildSnowflake
import utils.RedditClient

class Noods : Extension() {

    private val kordClient: Kord by inject()

    private var redditClient = RedditClient.getClient()

    private val noodCatalog = DataProvider.getNoodsData()


    override val name: String
        get() = "noods"

    class NoodsArguments : Arguments() {
        val category by defaultingString("category", "What will tickle your pickle?", "random")
    }

    class NoodsSlashArguments : Arguments() {
        val categoryName by defaultingString("category", "What will tickle your pickle?", "random")
    }
    override suspend fun setup() {
        chatCommand(::NoodsArguments) {

            name = "noods"
            description = "Finds some spicy noods."
            action {
                if (kordClient.getChannel(channel.id)?.data?.nsfw?.orElse(false) == false) {
                    this@action.message.respond("Eh, you lost boi??")
                } else {
                    val categoryToFetch = noodCatalog.filter { it.categoryName.contains(arguments.category) }
                    val category = categoryToFetch[0].value[kotlin.math.floor(Math.random() * categoryToFetch.size).toInt()]
                    val posts = redditClient.subreddit(category).posts().sorting(SubredditSort.HOT).limit(kotlin.math
                        .floor(Math.random() * 69).toInt()).build()
                    val data = posts.next()
                    var noodUrl = data.children[kotlin.math.floor(Math.random() * data.children.size).toInt()].url
                    while (!(noodUrl.contains(".jpg") or noodUrl.contains(".png"))){
                        noodUrl = data.children[kotlin.math.floor(Math.random() * data.children.size).toInt()].url
                    }
                    if (noodUrl != null) {
                        message.channel.createEmbed {
                            title = "Go. Enjoi those 10 seconds."
                            description = categoryToFetch[0].fullName
                            image = noodUrl
                            footer = message.getEmbedFooter()
                            timestamp = Clock.System.now()
                        }
                    }
                }
            }
        }

        publicSlashCommand(::NoodsSlashArguments) {
            name = "noods"
            description = "Finds some spicy noods."
            guild(getTestGuildSnowflake())
            action {
                if (kordClient.getChannel(channel.id)?.data?.nsfw?.orElse(false) == false) {
                    this@action.respond {
                        content = "Eh, you lost boi??"
                    }
                } else {
                    val categoryToFetch = noodCatalog.filter { it.categoryName.contains(arguments.categoryName) }
                    val category = categoryToFetch[0].value[kotlin.math.floor(Math.random() * categoryToFetch.size).toInt()]
                    val posts = redditClient.subreddit(category).posts().sorting(SubredditSort.HOT).limit(kotlin.math
                        .floor(Math.random() * 69).toInt()).build()
                    val data = posts.next()
                    var noodUrl = data.children[kotlin.math.floor(Math.random() * data.children.size).toInt()].url
                    while (!(noodUrl.contains(".jpg") or noodUrl.contains(".png"))) {
                        noodUrl = data.children[kotlin.math.floor(Math.random() * data.children.size).toInt()].url
                    }
                    if (noodUrl != null) {
                        respond {
                            embed {
                                title = "Go. Enjoi those 10 seconds."
                                description = categoryToFetch[0].fullName
                                image = noodUrl
                                footer = kordClient.getEmbedFooter()
                                timestamp = Clock.System.now()
                            }
                        }
                    }
                }
            }
        }
    }
}
