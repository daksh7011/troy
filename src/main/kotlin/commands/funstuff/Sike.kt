package commands.funstuff

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond

class Sike : Extension() {

    override val name: String
        get() = "sike"

    override suspend fun setup() {
        publicSlashCommand {
            name = "sike"
            description = "Sends sike whenever someone requests for it."
            action {
                respond { content = "** _S I K E_ **" }
            }
        }
    }
}
