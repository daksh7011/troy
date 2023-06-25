package commands.mod

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.Kord
import org.koin.core.component.inject
import utils.isOwner

class Reboot : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "reboot"

    override suspend fun setup() {
        publicSlashCommand {
            name = "reboot"
            description = "Reboots bot"
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
