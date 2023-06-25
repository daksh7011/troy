package commands.funstuff

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.Kord
import dev.kord.rest.builder.message.create.embed
import dev.kord.x.emoji.Emojis.eight
import dev.kord.x.emoji.Emojis.five
import dev.kord.x.emoji.Emojis.four
import dev.kord.x.emoji.Emojis.keycapTen
import dev.kord.x.emoji.Emojis.nine
import dev.kord.x.emoji.Emojis.one
import dev.kord.x.emoji.Emojis.seven
import dev.kord.x.emoji.Emojis.six
import dev.kord.x.emoji.Emojis.three
import dev.kord.x.emoji.Emojis.two
import dev.kord.x.emoji.toReaction
import org.koin.core.component.inject
import utils.getEmbedFooter

class Poll : Extension() {

    private val kordClient: Kord by inject()

    private val reactions = listOf(
        one.toReaction(),
        two.toReaction(),
        three.toReaction(),
        four.toReaction(),
        five.toReaction(),
        six.toReaction(),
        seven.toReaction(),
        eight.toReaction(),
        nine.toReaction(),
        keycapTen.toReaction(),
    )

    override val name: String
        get() = "poll"

    inner class PollArguments : Arguments() {
        val title by string {
            name = "title"
            description = "Title for poll"
        }
        val options by string {
            name = "options"
            description = "Options for poll separated by comma"
        }
    }

    override suspend fun setup() {
        publicSlashCommand(::PollArguments) {
            name = "poll"
            description = "Gives a poll for provided options"
            action {
                val optionList = arguments.options.split(",")
                respond {
                    embed {
                        title = "Poll for ${arguments.title}"
                        footer = kordClient.getEmbedFooter()
                        optionList.forEachIndexed { index, option ->
                            field {
                                name = "Option ${index + 1}"
                                value = option
                                inline = true
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
