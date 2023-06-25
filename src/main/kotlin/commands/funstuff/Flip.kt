package commands.funstuff

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import kotlin.math.floor

class Flip : Extension() {

    override val name: String
        get() = "flip"

    override suspend fun setup() {
        publicSlashCommand {
            name = "flip"
            description = "Flips a coin for you."
            action {
                respond {
                    val result = if (floor(Math.random() * 2).toInt() == 0) {
                        "heads"
                    } else {
                        "tails"
                    }
                    content = "It's $result"
                }
            }
        }
    }
}
