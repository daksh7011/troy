package extensions

import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import core.BaseMessageCommand
import core.TroyExtension
import dev.kord.common.Color
import dev.kord.core.behavior.channel.createEmbed
import utils.Extensions.embedUrl

class CreditsExtension : TroyExtension() {

    override val name: String
        get() = "credit"

    class CreditsArguments : Arguments() {
        val command by string("command", "Bot command")
    }

    override suspend fun setup() {

        troyCommand(::CreditsArguments) {
            name = "credit"
            description = "Sends credit for given command."
            action {
                val command =
                    messageCommand.messageCommandsRegistry.getCommand(arguments.command, event) as BaseMessageCommand
                channel.createEmbed {
                    color = Color(212121)
                    field {
                        name = command.name
                        value = command.credits.embedUrl()
                    }
                }
            }
        }
    }
}
