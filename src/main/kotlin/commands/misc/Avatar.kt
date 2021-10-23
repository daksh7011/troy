package commands.misc

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalUser
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import utils.Extensions
import utils.Extensions.getEmbedFooter

class Avatar : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "avatar"

    inner class AvatarArguments : Arguments() {
        val user by optionalUser("user", "Get avatar of mentioned user or yours if no user is mentioned.")
    }

    override suspend fun setup() {
        chatCommand(::AvatarArguments) {
            name = "avatar"
            description = "Get the avatar URL of the tagged user, or your own avatar."
            action {
                if (arguments.user == null) {
                    message.channel.createEmbed {
                        title = "Your avatar"
                        url = message.author?.avatar?.url
                        image = message.author?.avatar?.url
                        footer = message.getEmbedFooter()
                        timestamp = Clock.System.now()
                    }
                } else {
                    message.channel.createEmbed {
                        title = "${arguments.user?.username}'s avatar"
                        url = arguments.user?.avatar?.url
                        image = arguments.user?.avatar?.url
                        footer = kordClient.getEmbedFooter()
                        timestamp = Clock.System.now()
                    }
                }
            }
        }
        publicSlashCommand(::AvatarArguments) {
            name = "avatar"
            description = "Get the avatar URL of the tagged user, or your own avatar."
            guild(Extensions.getTestGuildSnowflake())
            action {
                if (arguments.user == null) {
                    respond {
                        embed {
                            title = "Your avatar"
                            url = member?.asUser()?.avatar?.url
                            image = member?.asUser()?.avatar?.url
                            footer = kordClient.getEmbedFooter()
                            timestamp = Clock.System.now()
                        }
                    }
                } else {
                    respond {
                        embed {
                            title = "${arguments.user?.username}'s avatar"
                            url = arguments.user?.avatar?.url
                            image = arguments.user?.avatar?.url
                            footer = kordClient.getEmbedFooter()
                            timestamp = Clock.System.now()
                        }
                    }
                }
            }
        }
    }
}
