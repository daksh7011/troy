package troy.commands.funstuff

import dev.kord.core.Kord
import dev.kord.rest.builder.message.embed
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
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import org.koin.core.component.inject
import troy.utils.getEmbedFooter

class Poll : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "poll"

    inner class PollArguments : Arguments() {
        val title by string {
            name = "title".toKey()
            description = "Title for poll".toKey()
        }
        val options by string {
            name = "options".toKey()
            description = "Options for poll separated by comma".toKey()
        }
    }

    override suspend fun setup() {
        publicSlashCommand(::PollArguments) {
            name = "poll".toKey()
            description = "Gives a poll for provided options".toKey()
            action {
                // Split by comma and trim each option to remove extra spaces
                val optionList = arguments.options.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                // Check if we have any valid options
                if (optionList.isEmpty()) {
                    respond {
                        content = "Please provide at least one valid option for the poll."
                    }
                    return@action
                }

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
                }.let { response ->
                    // Process options and add reactions more efficiently
                    val validOptions = optionList.size.coerceAtMost(REACTIONS.size)
                    for (index in 0 until validOptions) {
                        response.message.addReaction(REACTIONS[index])
                    }
                }
            }
        }
    }

    companion object {
        // Pre-compute reactions list as a static property to avoid recreating it for each instance
        private val REACTIONS = listOf(
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
    }
}
