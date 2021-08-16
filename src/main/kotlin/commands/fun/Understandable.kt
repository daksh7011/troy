package commands.`fun`

import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalUser
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import core.TroyExtension

class Understandable : TroyExtension() {

    override val name: String
        get() = "understandable"

    inner class UnderstandableArgument : Arguments() {
        val user by optionalUser("user", "Which user you want to wish great day.")
    }

    override suspend fun setup() {
        troyCommand(::UnderstandableArgument) {
            name = "understandable"
            description = "Sends understandable have a great day on request."
            aliases = arrayOf("uhand")
            action {
                if (arguments.user == null) message.channel.createMessage("Understandable, Have a great day.")
                else message.channel.createMessage("${arguments.user?.mention}, Understandable, Have a great day.")
            }
        }
    }
}
