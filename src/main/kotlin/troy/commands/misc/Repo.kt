package troy.commands.misc

import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand

class Repo : Extension() {

    override val name: String
        get() = "repo"

    override suspend fun setup() {
        publicSlashCommand {
            name = "repo"
            description = "Returns Troy's GitLab repo url."
            action {
                respond {
                    content = "https://gitlab.com/technowolf/troy" +
                        "\nNote: Please checkout develop branch if you want to contribute."
                }
            }
        }
    }
}
