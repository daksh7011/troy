package commands.funstuff

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalUser
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond

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
