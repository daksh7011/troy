package troy.commands.mod

import dev.kord.common.entity.Permission
import dev.kord.core.Kord
import dev.kord.core.behavior.ban
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.embed
import dev.kordex.core.checks.hasPermission
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.coalescingString
import dev.kordex.core.commands.converters.impl.user
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import org.koin.core.component.inject
import troy.data.repository.BanLogsRepository
import troy.utils.getEmbedFooter
import troy.utils.isOwner
import kotlin.time.Clock

class Ban : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "ban"

    inner class BanArguments : Arguments() {
        val user by user {
            name = "user".toKey()
            description = "Which user do you want to ban?".toKey()
        }
        val reason by coalescingString {
            name = "reason".toKey()
            description = "Reason for the ban".toKey()
        }
    }

    override suspend fun setup() {
        val banLogsRepository: BanLogsRepository by inject()
        publicSlashCommand(::BanArguments) {
            name = "ban".toKey()
            description = "Bans user with reason.".toKey()
            check {
                hasPermission(Permission.Administrator)
                requireBotPermissions(Permission.BanMembers)
            }
            action {
                val moderator = "${member?.asUser()?.username}#${member?.asUser()?.discriminator}"
                val banReason = arguments.reason
                if (arguments.user.id.isOwner()) {
                    respond { content = CANT_HURT_GOD_MESSAGE }
                    return@action
                }
                try {
                    guild?.ban(user.id) {
                        reason = banReason
                    }
                    banLogsRepository.insertBanLog(arguments.user, banReason, moderator)
                    respond {
                        embed {
                            setupBannedEmbed(
                                user.mention,
                                banReason,
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
        private const val ERROR_MESSAGE = "Could not ban the user. Please check my hierarchy in guild roles. If everything looks in order, Please contact the bot developers."
        private const val BAN_EVENT_TITLE = "Ban Event"
        private const val BANNED_USER_FIELD = "Banned User"
        private const val REASON_FIELD = "Reason of ban"
        private const val BANNED_BY_FIELD = "Banned by"

        suspend fun EmbedBuilder.setupBannedEmbed(
            userMention: String,
            reason: String,
            bannedBy: String,
            kordClient: Kord
        ) {
            title = BAN_EVENT_TITLE
            field {
                name = BANNED_USER_FIELD
                value = userMention
                inline = true
            }
            field {
                name = REASON_FIELD
                value = reason
                inline = true
            }
            field {
                name = BANNED_BY_FIELD
                value = bannedBy
            }
            timestamp = Clock.System.now()
            footer = kordClient.getEmbedFooter()
        }
    }
}
