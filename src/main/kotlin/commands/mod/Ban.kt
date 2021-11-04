package commands.mod

import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescedString
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Permission
import dev.kord.core.Kord
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.User
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.inject
import utils.Environment
import utils.Extensions
import utils.Extensions.getEmbedFooter

class Ban : Extension() {

    val kordClient: Kord by inject()

    override val name: String
        get() = "ban"

    inner class BanArguments : Arguments() {
        val user by user("user", "Which user do you want to ban?")
        val reason by coalescedString("reason", "Reason for the ban")
    }

    override suspend fun setup() {
        if (env(Environment.IS_DEBUG).toBoolean()) {
            Database.connect("jdbc:sqlite:src/main/kotlin/data/troy.db", "org.sqlite.JDBC")
        } else {
            Database.connect("jdbc:sqlite:troy.db", "org.sqlite.JDBC")
        }
        chatCommand(::BanArguments) {
            name = "ban"
            description = "Bans user with reason."
            check {
                hasPermission(Permission.Administrator)
                requireBotPermissions(Permission.BanMembers)
            }
            action {
                val user = arguments.user
                val banReason = arguments.reason
                message.getGuild().ban(user.id) {
                    reason = banReason
                }
                transaction {
                    insertBanLog(user, banReason, member?.mention.orEmpty())
                }
                message.channel.createEmbed {
                    setupBannedEmbed(
                        arguments.user.mention,
                        arguments.reason,
                        message.author?.mention.orEmpty()
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
            guild(Extensions.getTestGuildSnowflake())
            action {
                val user = arguments.user
                val banReason = arguments.reason
                guild?.ban(user.id) {
                    reason = banReason
                }
                transaction {
                    insertBanLog(user, banReason, member?.mention.orEmpty())
                }
                respond {
                    embed {
                        setupBannedEmbed(
                            user.mention,
                            banReason,
                            member?.mention.orEmpty()
                        )
                    }
                }
            }
        }
    }

    private fun Transaction.insertBanLog(
        user: User,
        banReason: String,
        moderator: String
    ): InsertStatement<Number> {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(BanLogs)
        return BanLogs.insert {
            it[bannedUser] = "${user.username}#${user.discriminator}"
            it[reason] = banReason
            it[bannedAt] = Clock.System.now().toString()
            it[bannedBy] = moderator
        }
    }

    private suspend fun EmbedBuilder.setupBannedEmbed(
        userMention: String,
        reason: String,
        bannedBy: String
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

object BanLogs : IntIdTable() {
    val bannedUser = varchar("bannedUser", 100)
    val reason = varchar("reason", 200)
    val bannedAt = varchar("timestamp", 100)
    val bannedBy = varchar("bannedBy", 100)
}
