package commands.`fun`

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import utils.Extensions.getEmbedFooter

class ItsOur : Extension() {

    private val kordClient: Kord by inject()

    private val imageUrl = "https://i.kym-cdn.com/entries/icons/original/000/034/467/Communist_Bugs_Bunny_Banner.jpg"

    override val name: String
        get() = "our"

    override suspend fun setup() {
        chatCommand {
            name = "our"
            description = "Firmly states that it is ours in this soviet soil."
            aliases = arrayOf("its-our")
            action {
                message.channel.createEmbed {
                    title = "It's our"
                    image = imageUrl
                    footer = message.getEmbedFooter()
                    timestamp = Clock.System.now()
                }
            }
        }

        publicSlashCommand {
            name = "our"
            description = "Firmly states that it is ours in this soviet soil."
            action {
                respond {
                    embed {
                        title = "It's our"
                        image = imageUrl
                        footer = kordClient.getEmbedFooter()
                        timestamp = Clock.System.now()
                    }
                }
            }
        }
    }
}
