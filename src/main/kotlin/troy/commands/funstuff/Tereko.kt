package troy.commands.funstuff

import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalUser
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand

class Tereko : Extension() {

    override val name: String
        get() = "tereko"

    inner class TerekoArgument : Arguments() {
        val user by optionalUser {
            name = "user"
            description = "hmm? :thinking:"
        }
    }

    override suspend fun setup() {
        publicSlashCommand(::TerekoArgument) {
            name = "tereko"
            description = "Returns tereko lagta hai."
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
