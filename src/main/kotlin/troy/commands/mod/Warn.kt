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
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import troy.commands.mod.Ban.Companion.setupBannedEmbed
import troy.commands.mod.Kick.Companion.setupKickedEmbed
import troy.data.models.WarningMode
import troy.data.repository.BanLogsRepository
import troy.data.repository.GlobalGuildRepository
import troy.data.repository.KickLogsRepository
import troy.data.repository.WarningLogsRepository
import troy.utils.getEmbedFooter
import troy.utils.isOwner

class Warn : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "warn"

    inner class WarnArguments : Arguments() {
        val warnedUser by user {
            name = "user".toKey()
            description = "Which user do you want to warn?".toKey()
        }
        val reason by coalescingString {
            name = "reason".toKey()
            description = "Reason for the warning.".toKey()
        }
    }

    override suspend fun setup() {
        val globalGuildRepository: GlobalGuildRepository by inject()
        val warningLogsRepository: WarningLogsRepository by inject()
        val kickLogsRepository: KickLogsRepository by inject()
        val banLogsRepository: BanLogsRepository by inject()

        publicSlashCommand(::WarnArguments) {
            name = "warn".toKey()
            description = "Warns the user with a reason.".toKey()
            check {
                hasPermission(Permission.Administrator)
                requireBotPermissions(Permission.KickMembers)
                requireBotPermissions(Permission.BanMembers)
            }
            action {
                val guildId = guild?.asGuild()?.id?.toString().orEmpty()
                val userId = arguments.warnedUser.id.toString()
                val warnReason = arguments.reason
                val moderator = "${member?.asUser()?.username}#${member?.asUser()?.discriminator}"

                if (arguments.warnedUser.id.isOwner()) {
                    respond { content = CANT_HURT_GOD_MESSAGE }
                    return@action
                }

                warningLogsRepository.insertUserWarning(arguments.warnedUser, warnReason, moderator)

                val userWarnings = warningLogsRepository.getUserWarningsCount(userId)
                val guildMaxWarnings = globalGuildRepository.getMaxWarningsForGuild(guildId)
                val didMaxWarningExceeded = warningLogsRepository.didUserExceedWarnings(guildMaxWarnings, userWarnings)
                val guildWarnMode = globalGuildRepository.getWarnModeForGuild(guildId)

                respond {
                    if (didMaxWarningExceeded) {
                        val reason = MAX_WARNINGS_EXCEEDED
                        when (guildWarnMode) {
                            is WarningMode.Kick -> try {
                                kickLogsRepository.insertKickLog(arguments.warnedUser, reason, "Troy")
                                warningLogsRepository.deleteWarningsForUser(arguments.warnedUser.id.toString())
                                guild?.kick(arguments.warnedUser.id, reason)
                                embed {
                                    setupKickedEmbed(
                                        arguments.warnedUser.mention,
                                        reason,
                                        member?.mention.orEmpty(),
                                        kordClient,
                                    )
                                }
                            } catch (exception: Exception) {
                                respond {
                                    content = KICK_ERROR_MESSAGE
                                }
                            }
                            is WarningMode.Ban -> try {
                                banLogsRepository.insertBanLog(arguments.warnedUser, reason, "Troy")
                                warningLogsRepository.deleteWarningsForUser(arguments.warnedUser.id.toString())
                                guild?.ban(arguments.warnedUser.id) { this.reason = reason }
                                embed {
                                    setupBannedEmbed(
                                        arguments.warnedUser.mention,
                                        reason,
                                        member?.mention.orEmpty(),
                                        kordClient,
                                    )
                                }
                            } catch (exception: Exception) {
                                respond {
                                    content = BAN_ERROR_MESSAGE
                                }
                            }
                            is WarningMode.None -> embed {
                                setupEmbedForNoneWarningMode(arguments.warnedUser.mention)
                            }
                        }
                    } else {
                        embed {
                            setupWarningEmbed(
                                arguments.warnedUser.mention,
                                warnReason,
                                member?.mention.orEmpty(),
                                "$userWarnings/$guildMaxWarnings",
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun EmbedBuilder.setupWarningEmbed(
        userMention: String,
        reason: String,
        warnedBy: String,
        warningsCount: String
    ) {
        title = WARNING_EVENT_TITLE
        field {
            name = WARNED_USER_FIELD
            value = userMention
            inline = true
        }
        field {
            name = REASON_FIELD
            value = reason
            inline = true
        }
        field {
            name = WARNED_BY_FIELD
            value = warnedBy
        }
        field {
            name = WARNINGS_FIELD
            value = warningsCount
        }
        timestamp = Clock.System.now()
        footer = kordClient.getEmbedFooter()
    }

    private suspend fun EmbedBuilder.setupEmbedForNoneWarningMode(userMention: String) {
        title = WARNING_EVENT_TITLE
        description = "$userMention $NONE_WARNING_MODE_DESCRIPTION"
        timestamp = Clock.System.now()
        footer = kordClient.getEmbedFooter()
    }

    companion object {
        private const val CANT_HURT_GOD_MESSAGE = "You can't hurt the god!"
        private const val MAX_WARNINGS_EXCEEDED = "Max warnings exceeded!"
        private const val KICK_ERROR_MESSAGE = "Could not kick the user. Please check my hierarchy in guild roles. If everything looks in order, Please contact the bot developers."
        private const val BAN_ERROR_MESSAGE = "Could not ban the user. Please check my hierarchy in guild roles. If everything looks in order, Please contact the bot developers."
        private const val WARNING_EVENT_TITLE = "Warning Event"
        private const val WARNED_USER_FIELD = "Warned User"
        private const val REASON_FIELD = "Reason of warning"
        private const val WARNED_BY_FIELD = "Warned by"
        private const val WARNINGS_FIELD = "Warnings"
        private const val NONE_WARNING_MODE_DESCRIPTION = "exceeded maximum warnings but warning mode is set to **None** for this guild. " +
            "Hence no action is taken. To modify this behaviour, Please change the warning mode for automated " +
            "Kick or Bans when user exceeds max number of warnings."
    }
}
