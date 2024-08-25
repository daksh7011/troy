package troy.commands.misc

import dev.kord.core.Kord
import dev.kord.rest.builder.message.embed
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalUser
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import troy.utils.getEmbedFooter

class Avatar : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "avatar"

    inner class AvatarArguments : Arguments() {
        val user by optionalUser {
            name = "user"
            description = "Get avatar of mentioned user or yours if no user is mentioned."
        }
    }

    override suspend fun setup() {
        publicSlashCommand(::AvatarArguments) {
            name = "avatar"
            description = "Get the avatar URL of the tagged user, or your own avatar."
            action {
                if (arguments.user == null) {
                    val memberAvatarUrl = member?.asUser()?.avatar?.cdnUrl?.toUrl()
                    respond {
                        embed {
                            title = "Your avatar"
                            url = memberAvatarUrl
                            image = memberAvatarUrl
                            footer = kordClient.getEmbedFooter()
                            timestamp = Clock.System.now()
                        }
                    }
                } else {
                    val argumentUserAvatarUrl = arguments.user?.avatar?.cdnUrl?.toUrl()
                    respond {
                        embed {
                            title = "${arguments.user?.username}'s avatar"
                            url = argumentUserAvatarUrl
                            image = argumentUserAvatarUrl
                            footer = kordClient.getEmbedFooter()
                            timestamp = Clock.System.now()
                        }
                    }
                }
            }
        }
    }
}
