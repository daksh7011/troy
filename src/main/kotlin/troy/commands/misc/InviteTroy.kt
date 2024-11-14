package troy.commands.misc

import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey

class InviteTroy : Extension() {

    override val name: String
        get() = "inviteTroy"

    override suspend fun setup() {
        publicSlashCommand {
            name = "inviteTroy".toKey()
            description = "Returns invite link for Troy.".toKey()
            action {
                respond {
                    content =
                        "https://discord.com/oauth2/authorize?client_id=871836869493661736&permissions=397820423367&scope=bot%20applications.commands"
                }
            }
        }
    }
}
