package commands.funstuff

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.Kord
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import utils.getEmbedFooter

class ItsOur : Extension() {

    private val kordClient: Kord by inject()

    private val imageUrl = "https://i.kym-cdn.com/entries/icons/original/000/034/467/Communist_Bugs_Bunny_Banner.jpg"

    override val name: String
        get() = "our"

    override suspend fun setup() {
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
