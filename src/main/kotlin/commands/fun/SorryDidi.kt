package commands.`fun`

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalUser
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond

class SorryDidi : Extension() {

    override val name: String
        get() = "sorry-didi"

    inner class SorryDidiArguments : Arguments() {
        val user by optionalUser("user", "Which user you want to wish great day.")
    }

    override suspend fun setup() {
        chatCommand(::SorryDidiArguments) {
            name = "sorry-didi"
            description = "Apologizes to woke didis out there."
            aliases = arrayOf("sorry", "sry", "sry-didi", "woke")
            action {
                if (arguments.user == null) message.channel.createMessage("Sorry woke didi.")
                else message.channel.createMessage("${arguments.user?.mention}, Sorry woke didi.")
            }
        }
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
