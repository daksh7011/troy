package commands.mod

import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescedString
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import data.repository.BanLogsRepository
import dev.kord.common.entity.Permission
import dev.kord.core.Kord
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import utils.getEmbedFooter

class Ban : Extension() {

    val kordClient: Kord by inject()

    override val name: String
        get() = "ban"

    inner class BanArguments : Arguments() {
        val user by user("user", "Which user do you want to ban?")
        val reason by coalescedString("reason", "Reason for the ban")
    }

    override suspend fun setup() {
        val banLogsRepository: BanLogsRepository by inject()
        chatCommand(::BanArguments) {
            name = "ban"
            description = "Bans user with reason."
            check {
                hasPermission(Permission.Administrator)
                requireBotPermissions(Permission.BanMembers)
            }
            action {
                val moderator = "${message.author?.username}#${message.author?.discriminator}"
                val banReason = arguments.reason
                message.getGuild().ban(arguments.user.id) {
                    reason = banReason
                }
                banLogsRepository.insertBanLog(arguments.user, banReason, moderator)
                message.channel.createEmbed {
                    setupBannedEmbed(
                        arguments.user.mention,
                        arguments.reason,
                        message.author?.mention.orEmpty(),
                        kordClient,
                    )
                }
            }
        }
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
            }
        }
    }

    companion object {
        suspend fun EmbedBuilder.setupBannedEmbed(
            userMention: String,
            reason: String,
            bannedBy: String,
            kordClient: Kord,
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
