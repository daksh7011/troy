package commands.misc

import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import core.BaseMessageCommand
import core.TroyExtension
import dev.kord.common.Color
import dev.kord.core.behavior.channel.createEmbed
import utils.Extensions.embedUrl

class Credits : TroyExtension() {

    override val name: String
        get() = "credit"

    class CreditsArguments : Arguments() {
        val command by string("command", "Bot command")
    }

    override suspend fun setup() {

        troyCommand(Credits::CreditsArguments) {
            name = "credit"
            description = "Sends credit for given command."
            action {
                val command =
                    messageCommand.messageCommandsRegistry.getCommand(arguments.command, event) as? BaseMessageCommand
                if (command != null) {
                    channel.createEmbed {
                        color = Color(212121)
                        command.credits.forEach {
                            field {
                                name = it.name
                                value = it.embedUrl()
                            }
                        }

                    }
                } else {
                    channel.createMessage("`${arguments.command}` command is credited to no one. It just appeared.")
                }
            }
        }
    }
}
