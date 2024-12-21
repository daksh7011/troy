package troy.commands.funstuff

import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalUser
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey

class SorryDidi : Extension() {

    override val name: String
        get() = "sorry-didi"

    inner class SorryDidiArguments : Arguments() {
        val user by optionalUser {
            name = "user".toKey()
            description = "Which woke didi you want to apologise to?.".toKey()
        }
    }

    override suspend fun setup() {
        publicSlashCommand(::SorryDidiArguments) {
            name = "sorry-didi".toKey()
            description = "Apologizes to woke didis out there.".toKey()
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
