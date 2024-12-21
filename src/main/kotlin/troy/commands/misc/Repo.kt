package troy.commands.misc

import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey

class Repo : Extension() {

    override val name: String
        get() = "repo"

    override suspend fun setup() {
        publicSlashCommand {
            name = "repo".toKey()
            description = "Returns Troy's Github repo url.".toKey()
            action {
                respond {
                    content = "https://github.com/daksh7011/troy" +
                            "\nNote: Please checkout develop branch if you want to contribute."
                }
            }
        }
    }
}
