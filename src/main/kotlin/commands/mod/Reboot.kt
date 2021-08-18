package commands.mod

import core.TroyExtension
import dev.kord.core.Kord
import org.koin.core.component.inject
import utils.Extensions.isOwner

class Reboot : TroyExtension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "reboot"

    override suspend fun setup() {
        troyCommand {
            name = "reboot"
            description = "Reboots the bot."
            action {
                if (message.author?.id?.isOwner()?.not() != false) {
                    message.channel.createMessage("This is owner only command.")
                } else {
                    message.channel.createMessage("Commencing reboot.")
                    kordClient.logout()
                    kordClient.shutdown()
                }
            }
        }
    }
}
