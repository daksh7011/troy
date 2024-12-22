package troy.commands.funstuff

import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalUser
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey

class Understandable : Extension() {

    override val name: String
        get() = "understandable"

    inner class UnderstandableArgument : Arguments() {
        val user by optionalUser {
            name = "user".toKey()
            description = "Which user you want to wish great day.".toKey()
        }
    }

    override suspend fun setup() {
        publicSlashCommand(::UnderstandableArgument) {
            name = "understandable".toKey()
            description = "Sends understandable have a great day on request.".toKey()
            action {
                if (arguments.user == null) {
                    respond {
                        content = "Understandable, Have a great day."
                    }
                } else {
                    respond {
                        content = "${arguments.user?.mention}, Understandable, Have a great day."
                    }
                }
            }
        }
    }
}
