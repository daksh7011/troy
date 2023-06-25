package commands.mod

import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescingString
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import data.repository.BanLogsRepository
import dev.kord.common.entity.Permission
import dev.kord.core.Kord
import dev.kord.core.behavior.ban
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import utils.getEmbedFooter
import utils.isOwner

class Ban : Extension() {

    val kordClient: Kord by inject()

    override val name: String
        get() = "ban"

    inner class BanArguments : Arguments() {
        val user by user {
            name = "user"
            description = "Which user do you want to ban?"
        }
        val reason by coalescingString {
            name = "reason"
            description = "Reason for the ban"
        }
    }

    override suspend fun setup() {
        val banLogsRepository: BanLogsRepository by inject()
        publicSlashCommand(::BanArguments) {
            name = "ban"
            description = "Bans user with reason."
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
                                kordClient
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
