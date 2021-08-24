package commands.`fun`

import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.commands.converters.impl.stringList
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import com.kotlindiscord.kord.extensions.commands.slash.AutoAckType
import com.kotlindiscord.kord.extensions.utils.addReaction
import core.TroyExtension
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.rest.builder.message.create.embed
import org.koin.core.component.inject
import utils.Extensions.getEmbedFooter
import utils.Extensions.getTestGuildSnowflake

class Poll : TroyExtension() {

    private val kordClient: Kord by inject()

    private val reactions = listOf("1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟")

    override val name: String
        get() = "poll"

    inner class PollArguments : Arguments() {
        val title by string("title", "Title for poll")
        val options by stringList("options", "Options for poll")
    }

    inner class PollSlashArguments : Arguments() {
        val title by string("title", "Title for poll")
        val options by string("options", "Options for poll seperated by comma.")
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
                            name = "Option ${index + 1}"
                            value = option
                        }
                    }
                }
                arguments.options.forEachIndexed { index, _ ->
                    sentEmbed.addReaction(reactions[index])
                }
            }
        }
        slashCommand(::PollSlashArguments) {
            name = "poll"
            description = "Gives a poll for provided options"
            autoAck = AutoAckType.PUBLIC
            guild(getTestGuildSnowflake())
            action {
                val optionList = arguments.options.split(",")
                publicFollowUp {
                    embed {
                        title = "Poll for ${arguments.title}"
                        footer = kordClient.getEmbedFooter()
                        optionList.forEachIndexed { index, option ->
                            field {
                                name = "Option ${index + 1}"
                                value = option
                            }
                        }
                        field {
                            name = "Note"
                            value = "Make sure you add comma after each option for poll."
                        }
                    }
                }.let {
                    optionList.forEachIndexed { index, _ ->
                        it.message.addReaction(reactions[index])
                    }
                }
            }
        }
    }
}
