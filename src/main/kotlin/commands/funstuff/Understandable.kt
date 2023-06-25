package commands.funstuff

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalUser
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond

class Understandable : Extension() {

    override val name: String
        get() = "understandable"

    inner class UnderstandableArgument : Arguments() {
        val user by optionalUser {
            name = "user"
            description = "Which user you want to wish great day."
        }
    }

    override suspend fun setup() {
        publicSlashCommand(::UnderstandableArgument) {
            name = "understandable"
            description = "Sends understandable have a great day on request."
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
