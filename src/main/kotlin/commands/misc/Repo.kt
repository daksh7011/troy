package commands.misc

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond

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
