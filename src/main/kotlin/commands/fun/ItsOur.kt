package commands.`fun`

import com.kotlindiscord.kord.extensions.commands.slash.AutoAckType
import com.kotlindiscord.kord.extensions.utils.env
import core.TroyExtension
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import utils.Environment
import utils.Extensions.getEmbedFooter

class ItsOur : TroyExtension() {

    private val kordClient: Kord by inject()

    private val imageUrl = "https://i.kym-cdn.com/entries/icons/original/000/034/467/Communist_Bugs_Bunny_Banner.jpg"

    override val name: String
        get() = "our"

    override suspend fun setup() {
        command {
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

        slashCommand {
            name = "our"
            description = "Firmly states that it is ours in this soviet soil."
            autoAck = AutoAckType.PUBLIC
            env(Environment.TEST_GUILD_ID)?.toLong()?.let { guild(Snowflake(it)) }
            action {
                publicFollowUp {
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
