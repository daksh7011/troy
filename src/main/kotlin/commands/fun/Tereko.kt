package commands.`fun`

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalUser
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond

class Tereko : Extension() {

    override val name: String
        get() = "tereko"

    inner class TerekoArgument : Arguments() {
        val user by optionalUser("user", "Which user you want to wish great day.")
    }

    override suspend fun setup() {
        chatCommand(::TerekoArgument) {
            name = "tereko"
            description = "Returns tereko lagta hai."
            aliases = arrayOf("tlh")
            action {
                if (arguments.user == null) message.channel.createMessage("Tereko lagta hai, But hai nai.")
                else message.channel.createMessage("${arguments.user?.mention}, Tereko lagta hai, But hai nai.")
            }
        }
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
