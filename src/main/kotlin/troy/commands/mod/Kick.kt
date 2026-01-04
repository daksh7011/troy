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
import org.koin.core.component.inject
import troy.data.repository.KickLogsRepository
import troy.utils.getEmbedFooter
import troy.utils.isOwner
import kotlin.time.Clock

class Kick : Extension() {

    private val kordClient: Kord by inject()

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
                    respond { content = CANT_HURT_GOD_MESSAGE }
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
                        content = ERROR_MESSAGE
                    }
                }
            }
        }
    }

    companion object {
        private const val CANT_HURT_GOD_MESSAGE = "You can't hurt the god!"
        private const val ERROR_MESSAGE = "Could not kick the user. Please check my hierarchy in guild roles. If everything looks in order, Please contact the bot developers."
        private const val KICK_EVENT_TITLE = "Kick Event"
        private const val KICKED_USER_FIELD = "Kicked User"
        private const val REASON_FIELD = "Reason of kick"
        private const val KICKED_BY_FIELD = "Kicked by"

        suspend fun EmbedBuilder.setupKickedEmbed(
            userMention: String,
            reason: String,
            kickedBy: String,
            kordClient: Kord
        ) {
            title = KICK_EVENT_TITLE
            field {
                name = KICKED_USER_FIELD
                value = userMention
                inline = true
            }
            field {
                name = REASON_FIELD
                value = reason
                inline = true
            }
            field {
                name = KICKED_BY_FIELD
                value = kickedBy
            }
            timestamp = Clock.System.now()
            footer = kordClient.getEmbedFooter()
        }
    }
}
