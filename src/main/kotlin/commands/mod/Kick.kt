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
import utils.Extensions.getEmbedFooter

class Kick : Extension() {

    val kordClient: Kord by inject()

    override val name: String
        get() = "kick"

    inner class KickArguments : Arguments() {
        val user by user("user", "Which user do you want to kick?")
        val reason by coalescedString("reason", "Reason for the kick")
    }

    override suspend fun setup() {
        if (env(Environment.IS_DEBUG).toBoolean()) {
            Database.connect("jdbc:sqlite:src/main/kotlin/data/troy.db", "org.sqlite.JDBC")
        } else {
            Database.connect("jdbc:sqlite:troy.db", "org.sqlite.JDBC")
        }
        chatCommand(::KickArguments) {
            name = "kick"
            description = "Kicks user with reason."
            check {
                hasPermission(Permission.Administrator)
                requireBotPermissions(Permission.KickMembers)
            }
            action {
                val user = arguments.user
                val kickReason = arguments.reason
                val moderator = "${message.author?.username}#${message.author?.discriminator}"
                message.getGuild().kick(user.id, kickReason)
                transaction {
                    insertKickLog(user, kickReason, moderator)
                }
                message.channel.createEmbed {
                    setupKickedEmbed(
                        arguments.user.mention,
                        arguments.reason,
                        message.author?.mention.orEmpty(),
                        kordClient,
                    )
                }
            }
        }
        publicSlashCommand(::KickArguments) {
            name = "kick"
            description = "Kicks user with reason."
            check {
                hasPermission(Permission.Administrator)
                requireBotPermissions(Permission.KickMembers)
            }
            action {
                val user = arguments.user
                val kickReason = arguments.reason
                val moderator = "${member?.asUser()?.username}#${member?.asUser()?.discriminator}"
                guild?.kick(user.id, kickReason)
                transaction {
                    insertKickLog(user, kickReason, moderator)
                }
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
            }
        }
    }

    companion object {
        fun Transaction.insertKickLog(
            user: User,
            kickReason: String,
            moderator: String
        ): InsertStatement<Number> {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(KickLogs)
            return KickLogs.insert {
                it[kickedUser] = "${user.username}#${user.discriminator}"
                it[reason] = kickReason
                it[kickedAt] = Clock.System.now().toString()
                it[kickedBy] = moderator
            }
        }

        suspend fun EmbedBuilder.setupKickedEmbed(
            userMention: String,
            reason: String,
            kickedBy: String,
            kordClient: Kord,
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

object KickLogs : IntIdTable() {
    val kickedUser = varchar("kickedUser", 100)
    val reason = varchar("reason", 200)
    val kickedAt = varchar("timestamp", 100)
    val kickedBy = varchar("kickedBy", 100)
}
