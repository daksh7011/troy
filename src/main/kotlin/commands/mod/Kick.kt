package commands.mod

import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import data.repository.KickLogsRepository
import dev.kord.common.entity.Permission
import dev.kord.core.Kord
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import utils.getEmbedFooter
import utils.isOwner

class Kick : Extension() {

    val kordClient: Kord by inject()

    override val name: String
        get() = "kick"

    inner class KickArguments : Arguments() {
        val user by user {
            name = "user"
            description = "Which user do you want to kick?"
        }
        val reason by string {
            name = "reason"
            description = "Reason for the kick"
        }
    }

    override suspend fun setup() {
        val kickLogsRepository: KickLogsRepository by inject()
        publicSlashCommand(::KickArguments) {
            name = "kick"
            description = "Kicks user with reason."
            check {
                hasPermission(Permission.Administrator)
                requireBotPermissions(Permission.KickMembers)
            }
            action {
                val kickReason = arguments.reason
                val moderator = "${member?.asUser()?.username}#${member?.asUser()?.discriminator}"

                if (arguments.user.id.isOwner()) {
                    respond { content = "You can't hurt the god!" }
                    return@action
                }
                try {
                    guild?.kick(user.id, kickReason)
                    kickLogsRepository.insertKickLog(arguments.user, kickReason, moderator)
                    respond {
                        embed {
                            setupKickedEmbed(
                                user.mention,
                                kickReason,
                                member?.mention.orEmpty(),
                                kordClient,
                            )
                        }
                    }
                } catch (exception: Exception) {
                    respond {
                        content = "Could not kick the user. Please check my hierarchy in guild roles." +
                            " If everything looks in order, Please contact the bot developers."
                    }
                }
            }
        }
    }

    companion object {
        suspend fun EmbedBuilder.setupKickedEmbed(
            userMention: String,
            reason: String,
            kickedBy: String,
            kordClient: Kord
        ) {
            title = "Kick Event"
            field {
                name = "Kicked User"
                value = userMention
                inline = true
            }
            field {
                name = "Reason of kick"
                value = reason
                inline = true
            }
            field {
                name = "Kicked by"
                value = kickedBy
            }
            timestamp = Clock.System.now()
            footer = kordClient.getEmbedFooter()
        }
    }
}
