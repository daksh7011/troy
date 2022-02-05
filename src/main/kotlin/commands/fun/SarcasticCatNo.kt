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
import utils.getEmbedFooter

class SarcasticCatNo : Extension() {

    private val kordClient: Kord by inject()

    private val imageUrl = "https://cdn.discordapp.com/attachments/690818384321052714/829208012827787274/image0.png"

    override val name: String
        get() = "sarno"

    override suspend fun setup() {
        chatCommand {
            name = "sarno"
            description = "Summons a sarcastic catto to say no."
            action {
                message.channel.createEmbed {
                    title = "NO"
                    image = imageUrl
                    footer = message.getEmbedFooter()
                    timestamp = Clock.System.now()
                }
            }
        }

        publicSlashCommand {
            name = "sarno"
            description = "Summons a sarcastic catto to say no."
            action {
                respond {
                    embed {
                        title = "NO"
                        image = imageUrl
                        footer = kordClient.getEmbedFooter()
                        timestamp = Clock.System.now()
                    }
                }
            }
        }
    }
}
