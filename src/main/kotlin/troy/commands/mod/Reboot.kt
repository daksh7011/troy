package troy.commands.mod

import dev.kord.core.Kord
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import org.koin.core.component.inject
import troy.utils.isOwner

class Reboot : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "reboot"

    override suspend fun setup() {
        publicSlashCommand {
            name = "reboot".toKey()
            description = "Reboots bot".toKey()
            check { failIf(!event.interaction.user.id.isOwner(), message = "This is owner only command.") }
            action {
                respond {
                    content = "Commencing reboot."
                }
                kordClient.logout()
                kordClient.shutdown()
            }
        }
    }
}
