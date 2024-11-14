package troy.commands.funstuff

import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalUser
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey

class Tereko : Extension() {

    override val name: String
        get() = "tereko"

    inner class TerekoArgument : Arguments() {
        val user by optionalUser {
            name = "user".toKey()
            description = "hmm? :thinking:".toKey()
        }
    }

    override suspend fun setup() {
        publicSlashCommand(::TerekoArgument) {
            name = "tereko".toKey()
            description = "Returns tereko lagta hai.".toKey()
            action {
                if (arguments.user == null) {
                    respond {
                        content = "Tereko lagta hai, But hai nai."
                    }
                } else {
                    respond {
                        content = "${arguments.user?.mention}, Tereko lagta hai, But hai nai."
                    }
                }
            }
        }
    }
}
