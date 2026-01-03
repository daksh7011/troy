package troy.commands.funstuff

import dev.kord.core.Kord
import dev.kord.rest.builder.message.embed
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import org.koin.core.component.inject
import troy.utils.getEmbedFooter
import kotlin.time.Clock

class SarcasticCatNo : Extension() {

    private val kordClient: Kord by inject()

    private val imageUrl = "https://cdn.discordapp.com/attachments/690818384321052714/829208012827787274/image0.png"

    override val name: String
        get() = "sarno"

    override suspend fun setup() {
        publicSlashCommand {
            name = "sarno".toKey()
            description = "Summons a sarcastic catto to say no.".toKey()
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
