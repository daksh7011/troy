package troy.commands.funstuff

import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand

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
