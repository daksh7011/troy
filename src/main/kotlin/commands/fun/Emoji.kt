package commands.`fun`

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescedString
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import utils.Extensions

class Emoji : Extension() {

    override val name: String
        get() = "emoji"

    inner class EmojiArguments : Arguments() {
        val emoji by coalescedString(
            "emoji-name",
            "Which emoji would you like me to send? PS: Animated emoji are supported."
        )
    }

    override suspend fun setup() {
        chatCommand(::EmojiArguments) {
            name = "emoji"
            description = "Sends server custom emoji, Also supports animated emojis."
            action {
                if (
                    guild?.emojis?.filter { it.name == arguments.emoji }?.count() != 0
                ) {
                    val emoji = guild?.emojis?.filter {
                        it.name == arguments.emoji
                    }?.first()
                    if (emoji != null) {
                        channel.createMessage(emoji.mention)
                    }
                } else {
                    channel.createMessage("Can't find that emoji on **${guild?.asGuild()?.name}**")
                }
            }
        }

        publicSlashCommand(::EmojiArguments) {
            name = "emoji"
            description = "Sends server custom emoji, Also supports animated emojis."
            guild(Extensions.getTestGuildSnowflake())
            action {
                if (
                    guild?.emojis?.filter { it.name == arguments.emoji }?.count() != 0
                ) {
                    val emoji = guild?.emojis?.filter {
                        it.name == arguments.emoji
                    }?.first()
                    if (emoji != null) {
                        respond { content = emoji.mention }
                    }
                } else {
                    respond { content = "Can't find that emoji on **${guild?.asGuild()?.name}**" }
                }
            }
        }
    }
}
