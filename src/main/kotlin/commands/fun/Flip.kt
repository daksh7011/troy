package commands.`fun`

import com.kotlindiscord.kord.extensions.commands.slash.AutoAckType
import core.TroyExtension
import utils.Extensions.getTestGuildSnowflake
import kotlin.math.floor

class Flip : TroyExtension() {

    override val name: String
        get() = "flip"

    override suspend fun setup() {
        troyCommand {
            name = "flip"
            description = "Flips a coin for you."
            action {
                val result = if (floor(Math.random() * 2).toInt() == 0) "heads"
                else "tails"
                message.channel.createMessage("It's $result")
            }
        }
        slashCommand {
            name = "flip"
            description = "Flips a coin for you."
            autoAck = AutoAckType.PUBLIC
            guild(getTestGuildSnowflake())
            action {
                publicFollowUp {
                    val result = if (floor(Math.random() * 2).toInt() == 0) "heads"
                    else "tails"
                    content = "It's $result"
                }
            }
        }
    }
}
