package troy.commands.funstuff

import dev.kord.core.Kord
import dev.kord.rest.builder.message.embed
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import troy.utils.getEmbedFooter

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
