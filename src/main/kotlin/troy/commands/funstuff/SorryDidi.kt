package troy.commands.funstuff

import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalUser
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand

class SorryDidi : Extension() {

    override val name: String
        get() = "sorry-didi"

    inner class SorryDidiArguments : Arguments() {
        val user by optionalUser {
            name = "user"
            description = "Which woke didi you want to apologise to?."
        }
    }

    override suspend fun setup() {
        publicSlashCommand(::SorryDidiArguments) {
            name = "sorry-didi"
            description = "Apologizes to woke didis out there."
            action {
                if (arguments.user == null) {
                    respond {
                        content = "Sorry woke didi."
                    }
                } else {
                    respond {
                        content = "${arguments.user?.mention}, Sorry woke didi."
                    }
                }
            }
        }
    }
}
