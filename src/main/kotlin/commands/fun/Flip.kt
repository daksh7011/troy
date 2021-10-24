package commands.`fun`

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import utils.Extensions.getTestGuildSnowflake
import kotlin.math.floor

class Flip : Extension() {

    override val name: String
        get() = "flip"

    override suspend fun setup() {
        chatCommand {
            name = "flip"
            description = "Flips a coin for you."
            action {
                val result = if (floor(Math.random() * 2).toInt() == 0) "heads"
                else "tails"
                message.channel.createMessage("It's $result")
            }
        }
        publicSlashCommand {
            name = "flip"
            description = "Flips a coin for you."
            guild(getTestGuildSnowflake())
            action {
                respond {
                    val result = if (floor(Math.random() * 2).toInt() == 0) "heads"
                    else "tails"
                    content = "It's $result"
                }
            }
        }
    }
}
