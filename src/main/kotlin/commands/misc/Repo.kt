package commands.misc

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import utils.Extensions.getTestGuildSnowflake

class Repo : Extension() {

    override val name: String
        get() = "repo"

    override suspend fun setup() {
        chatCommand {
            name = "repo"
            description = "Returns Troy's GitLab repo url."
            action {
                message.channel.createMessage(
                    "https://gitlab.com/technowolf/troy" +
                            "\nNote: Please checkout develop branch if you want to contribute."
                )
            }
        }
        publicSlashCommand {
            name = "repo"
            description = "Returns Troy's GitLab repo url."
            guild(getTestGuildSnowflake())
            action {
                respond {
                    content = "https://gitlab.com/technowolf/troy" +
                            "\nNote: Please checkout develop branch if you want to contribute."
                }
            }
        }
    }
}
