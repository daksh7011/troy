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
import troy.data.repository.BanLogsRepository
import troy.utils.getEmbedFooter
import troy.utils.isOwner

class Ban : Extension() {

    val kordClient: Kord by inject()

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
                    respond { content = "You can't hurt the god!" }
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
                        content = "Could not ban the user. Please check my hierarchy in guild roles." +
                            " If everything looks in order, Please contact the bot developers."
                    }
                }
            }
        }
    }

    companion object {
        suspend fun EmbedBuilder.setupBannedEmbed(
            userMention: String,
            reason: String,
            bannedBy: String,
            kordClient: Kord
        ) {
            title = "Ban Event"
            field {
                name = "Banned User"
                value = userMention
                inline = true
            }
            field {
                name = "Reason of ban"
                value = reason
                inline = true
            }
            field {
                name = "Banned by"
                value = bannedBy
            }
            timestamp = Clock.System.now()
            footer = kordClient.getEmbedFooter()
        }
    }
}
