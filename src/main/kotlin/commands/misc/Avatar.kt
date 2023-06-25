package commands.misc

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalUser
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.Kord
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import utils.getEmbedFooter

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
