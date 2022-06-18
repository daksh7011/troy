package commands.mod

import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescedString
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import commands.mod.Ban.Companion.setupBannedEmbed
import commands.mod.Kick.Companion.setupKickedEmbed
import data.models.WarningMode
import data.repository.BanLogsRepository
import data.repository.GlobalGuildRepository
import data.repository.KickLogsRepository
import data.repository.WarningLogsRepository
import dev.kord.common.entity.Permission
import dev.kord.core.Kord
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import utils.getEmbedFooter
import utils.isOwner

class Warn : Extension() {

    val kordClient: Kord by inject()

    override val name: String
        get() = "warn"

    inner class WarnArguments : Arguments() {
        val warnedUser by user("user", "Which user do you want to warn?")
        val reason by coalescedString("reason", "Reason for the warning.")
    }

    override suspend fun setup() {
        val globalGuildRepository: GlobalGuildRepository by inject()
        val warningLogsRepository: WarningLogsRepository by inject()
        val kickLogsRepository: KickLogsRepository by inject()
        val banLogsRepository: BanLogsRepository by inject()

        chatCommand(::WarnArguments) {
            name = "warn"
            description = "Warns the user with a reason."
            check {
                hasPermission(Permission.Administrator)
                requireBotPermissions(Permission.KickMembers)
                requireBotPermissions(Permission.BanMembers)
            }
            action {
                val guildId = message.getGuild().id.asString
                val userId = arguments.warnedUser.id.asString
                val warnReason = arguments.reason
                val moderator = "${message.author?.username}#${message.author?.discriminator}"

                if (arguments.warnedUser.id.isOwner()) {
                    message.channel.createMessage("You can't hurt the god!")
                    return@action
                }

                warningLogsRepository.insertUserWarning(arguments.warnedUser, warnReason, moderator)

                val userWarnings = warningLogsRepository.getUserWarningsCount(userId)
                val guildMaxWarnings = globalGuildRepository.getMaxWarningsForGuild(guildId)
                val didMaxWarningExceeded = warningLogsRepository.didUserExceedWarnings(guildMaxWarnings, userWarnings)
                val guildWarnMode = globalGuildRepository.getWarnModeForGuild(guildId)

                if (didMaxWarningExceeded) {
                    val reason = "Max warnings exceeded!"
                    when (guildWarnMode) {
                        is WarningMode.Kick -> {
                            try {
                                kickLogsRepository.insertKickLog(arguments.warnedUser, reason, "Troy")
                                warningLogsRepository.deleteWarningsForUser(userId)
                                message.getGuild().kick(arguments.warnedUser.id, reason)
                                message.channel.createEmbed {
                                    setupKickedEmbed(
                                        arguments.warnedUser.mention,
                                        reason,
                                        "<@${kordClient.selfId.asString}>",
                                        kordClient,
                                    )
                                }
                            } catch (exception: Exception) {
                                message.channel.createMessage(
                                    "Could not kick the user. Please check my hierarchy in guild roles."
                                )
                            }
                        }
                        is WarningMode.Ban -> {
                            try {
                                banLogsRepository.insertBanLog(arguments.warnedUser, reason, "Troy")
                                warningLogsRepository.deleteWarningsForUser(userId)
                                message.getGuild().ban(arguments.warnedUser.id) {
                                    this.reason = reason
                                }
                                message.channel.createEmbed {
                                    setupBannedEmbed(
                                        arguments.warnedUser.mention,
                                        reason,
                                        "<@${kordClient.selfId.asString}>",
                                        kordClient
                                    )
                                }
                            } catch (exception: Exception) {
                                message.channel.createMessage(
                                    "Could not ban the user. Please check my hierarchy in guild roles."
                                )
                            }
                        }
                        is WarningMode.None -> {
                            message.channel.createEmbed {
                                setupEmbedForNoneWarningMode(arguments.warnedUser.mention)
                            }
                        }
                    }
                } else {
                    message.channel.createEmbed {
                        setupWarningEmbed(
                            arguments.warnedUser.mention,
                            warnReason,
                            message.author?.mention.orEmpty(),
                            "$userWarnings/$guildMaxWarnings"
                        )
                    }
                }
            }
        }
        publicSlashCommand(::WarnArguments) {
            name = "warn"
            description = "Warns the user with a reason."
            check {
                hasPermission(Permission.Administrator)
                requireBotPermissions(Permission.KickMembers)
                requireBotPermissions(Permission.BanMembers)
            }
            action {
                val guildId = guild?.asGuild()?.id?.asString.orEmpty()
                val userId = arguments.warnedUser.id.asString
                val warnReason = arguments.reason
                val moderator = "${member?.asUser()?.username}#${member?.asUser()?.discriminator}"

                if (arguments.warnedUser.id.isOwner()) {
                    respond { content = "You can't hurt the god!" }
                    return@action
                }

                warningLogsRepository.insertUserWarning(arguments.warnedUser, warnReason, moderator)

                val userWarnings = warningLogsRepository.getUserWarningsCount(userId)
                val guildMaxWarnings = globalGuildRepository.getMaxWarningsForGuild(guildId)
                val didMaxWarningExceeded = warningLogsRepository.didUserExceedWarnings(guildMaxWarnings, userWarnings)
                val guildWarnMode = globalGuildRepository.getWarnModeForGuild(guildId)

                respond {
                    if (didMaxWarningExceeded) {
                        val reason = "Max warnings exceeded!"
                        when (guildWarnMode) {
                            is WarningMode.Kick -> {
                                try {
                                    kickLogsRepository.insertKickLog(arguments.warnedUser, reason, "Troy")
                                    warningLogsRepository.deleteWarningsForUser(arguments.warnedUser.id.asString)
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
                                        content = "Could not kick the user. Please check my hierarchy in guild roles." +
                                                " If everything looks in order, Please contact the bot developers."
                                    }
                                }
                            }
                            is WarningMode.Ban -> {
                                try {
                                    banLogsRepository.insertBanLog(arguments.warnedUser, reason, "Troy")
                                    warningLogsRepository.deleteWarningsForUser(arguments.warnedUser.id.asString)
                                    guild?.ban(arguments.warnedUser.id) { this.reason = reason }
                                    embed {
                                        setupBannedEmbed(
                                            arguments.warnedUser.mention,
                                            reason,
                                            member?.mention.orEmpty(),
                                            kordClient
                                        )
                                    }
                                } catch (exception: Exception) {
                                    respond {
                                        content = "Could not ban the user. Please check my hierarchy in guild roles." +
                                                " If everything looks in order, Please contact the bot developers."
                                    }
                                }
                            }
                            is WarningMode.None -> {
                                embed {
                                    setupEmbedForNoneWarningMode(arguments.warnedUser.mention)
                                }
                            }
                        }
                    } else {
                        embed {
                            setupWarningEmbed(
                                arguments.warnedUser.mention,
                                warnReason,
                                member?.mention.orEmpty(),
                                "$userWarnings/$guildMaxWarnings"
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
        warningsCount: String,
    ) {
        title = "Warning Event"
        field {
            name = "Warned User"
            value = userMention
            inline = true
        }
        field {
            name = "Reason of warning"
            value = reason
            inline = true
        }
        field {
            name = "Warned by"
            value = warnedBy
        }
        field {
            name = "Warnings"
            value = warningsCount
        }
        timestamp = Clock.System.now()
        footer = kordClient.getEmbedFooter()
    }

    private suspend fun EmbedBuilder.setupEmbedForNoneWarningMode(userMention: String) {
        title = "Warning Event"
        description = "$userMention exceeded maximum warnings but warning mode is set to **None** for this guild. " +
                "Hence no action is taken. To modify this behaviour, Please change the warning mode for automated " +
                "Kick or Bans when user exceeds max number of warnings."
        timestamp = Clock.System.now()
        footer = kordClient.getEmbedFooter()
    }
}
