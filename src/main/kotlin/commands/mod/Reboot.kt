package commands.mod

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import dev.kord.core.Kord
import org.koin.core.component.inject
import utils.Extensions.isOwner

class Reboot : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "reboot"

    override suspend fun setup() {
        chatCommand {
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
