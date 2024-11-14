package troy.commands.mod

import dev.kord.common.entity.Permission
import dev.kord.core.Kord
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.embed
import dev.kordex.core.checks.hasPermission
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.commands.converters.impl.user
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import troy.data.repository.KickLogsRepository
import troy.utils.getEmbedFooter
import troy.utils.isOwner

class Kick : Extension() {

    val kordClient: Kord by inject()

    override val name: String
        get() = "kick"

    inner class KickArguments : Arguments() {
        val user by user {
            name = "user".toKey()
            description = "Which user do you want to kick?".toKey()
        }
        val reason by string {
            name = "reason".toKey()
            description = "Reason for the kick".toKey()
        }
    }

    override suspend fun setup() {
        val kickLogsRepository: KickLogsRepository by inject()
        publicSlashCommand(::KickArguments) {
            name = "kick".toKey()
            description = "Kicks user with reason.".toKey()
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
