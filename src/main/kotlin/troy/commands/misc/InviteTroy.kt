package troy.commands.misc

import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand

class InviteTroy : Extension() {

    override val name: String
        get() = "inviteTroy"

    override suspend fun setup() {
        publicSlashCommand {
            name = "inviteTroy"
            description = "Returns invite link for Troy."
            action {
                respond {
                    content =
                        "https://discord.com/oauth2/authorize?client_id=871836869493661736&permissions=397820423367&scope=bot%20applications.commands"
                }
            }
        }
    }
}
