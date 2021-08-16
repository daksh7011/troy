package commands.`fun`

import core.TroyExtension
import dev.kord.core.behavior.channel.createEmbed
import kotlinx.datetime.Clock
import utils.Extensions.getEmbedFooter

class ItsOur : TroyExtension() {

    override val name: String
        get() = "our"

    override suspend fun setup() {
        troyCommand {
            name = "our"
            description = "Firmly states that it is ours in this soviet soil."
            aliases = arrayOf("its-our")
            action {
                val imageUrl =
                    "https://i.kym-cdn.com/entries/icons/original/000/034/467/Communist_Bugs_Bunny_Banner.jpg"
                message.channel.createEmbed {
                    title = "It's our"
                    image = imageUrl
                    footer = message.getEmbedFooter()
                    timestamp = Clock.System.now()
                }
            }
        }
    }
}
