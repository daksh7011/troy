package commands.`fun`

import core.TroyExtension
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
    }
}
