package commands.misc

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import utils.Extensions

class Invite : Extension() {

    override val name: String
        get() = "invite"

    override suspend fun setup() {
        publicSlashCommand {
            name = "invite"
            description = "Returns invite link for Troy."
            guild(Extensions.getTestGuildSnowflake())
            action {
                respond {
                    content = "https://discord.com/oauth2/authorize?client_id=871836869493661736&permissions=397820423367&scope=bot%20applications.commands"
                }
            }
        }
    }
}
