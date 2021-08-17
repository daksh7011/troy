package commands.`fun`

import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.commands.converters.impl.stringList
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import com.kotlindiscord.kord.extensions.utils.addReaction
import core.TroyExtension
import dev.kord.core.behavior.channel.createEmbed
import utils.Extensions.getEmbedFooter

class Poll : TroyExtension() {

    private val reactions = listOf("1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟")

    override val name: String
        get() = "poll"

    class PollArguments : Arguments() {
        val title by string("title", "Title for poll")
        val options by stringList("options", "Options for poll")
    }

    override suspend fun setup() {
        troyCommand(::PollArguments) {
            name = "poll"
            description = "Gives a poll for the options"
            action {
                val sentEmbed = message.channel.createEmbed {
                    title = "Poll for ${arguments.title}"
                    footer = message.getEmbedFooter()
                    arguments.options.forEachIndexed { index, option ->
                        field {
                            name = "Option $index+1"
                            value = option
                        }
                    }
                }
                arguments.options.forEachIndexed { index, _ ->
                    sentEmbed.addReaction(reactions[index])
                }
            }
        }
    }
}
