package troy.commands.funstuff

import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey

class Sike : Extension() {

    override val name: String
        get() = "sike"

    override suspend fun setup() {
        publicSlashCommand {
            name = "sike".toKey()
            description = "Sends sike whenever someone requests for it.".toKey()
            action {
                respond { content = "** _S I K E_ **" }
            }
        }
    }
}
