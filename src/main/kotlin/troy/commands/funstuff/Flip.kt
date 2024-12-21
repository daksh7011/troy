package troy.commands.funstuff

import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import kotlin.math.floor

class Flip : Extension() {

    override val name: String
        get() = "flip"

    override suspend fun setup() {
        publicSlashCommand {
            name = "flip".toKey()
            description = "Flips a coin for you.".toKey()
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
