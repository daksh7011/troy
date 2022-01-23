package commands.mod

import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescedString
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import commands.mod.Ban.Companion.insertBanLog
import commands.mod.Ban.Companion.setupBannedEmbed
import commands.mod.Kick.Companion.insertKickLog
import commands.mod.Kick.Companion.setupKickedEmbed
import data.GuildConfig
import data.WarnMode
import dev.kord.common.entity.Permission
import dev.kord.core.Kord
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.User
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.inject
import utils.Extensions
import utils.Extensions.getEmbedFooter

@Suppress("DuplicatedCode")
class Warn : Extension() {

    val kordClient: Kord by inject()

    override val name: String
        get() = "warn"

    inner class WarnArguments : Arguments() {
        val user by user("user", "Which user do you want to warn?")
        val reason by coalescedString("reason", "Reason for the warning.")
    }

    override suspend fun setup() {
        Extensions.connectToDatabase()
        chatCommand(::WarnArguments) {
            name = "warn"
            description = "Warns the user with a reason."
            check {
                hasPermission(Permission.Administrator)
                requireBotPermissions(Permission.KickMembers)
                requireBotPermissions(Permission.BanMembers)
            }
            action {
                val user = arguments.user
                val userSnowflake = user.id.value.toLong()
                val warnReason = arguments.reason
                val moderator = "${message.author?.username}#${message.author?.discriminator}"
                val guildSnowflake = message.getGuild().asGuild().id.value.toLong()
                var didMaxWarningExceeded = false
                var userWarnings = 0
                var guildMaxWarnings = 0
                var guildWarnMode: WarnMode? = null

                transaction {
                    addLogger(StdOutSqlLogger)
                    SchemaUtils.create(WarnLogs)
                    insertWarnLog(user, warnReason, moderator)
                    userWarnings = getUserWarnings(userSnowflake)
                    guildMaxWarnings = getMaxWarnings(guildSnowflake)
                    guildWarnMode = getGuildWarnMode(guildSnowflake)
                    didMaxWarningExceeded = isUserWarningsExceeded(guildSnowflake, userWarnings)
                }
                if (didMaxWarningExceeded) {
                    val reason = "Max warnings exceeded!"
                    when (guildWarnMode) {
                        WarnMode.Kick -> {
                            transaction {
                                insertKickLog(user, reason, "Troy")
                                deleteWarnLog(user.id.value.toLong())
                            }
                            message.getGuild().kick(user.id, reason)
                            message.channel.createEmbed {
                                setupKickedEmbed(
                                    user.mention,
                                    reason,
                                    "<@${kordClient.selfId.asString}>",
                                    kordClient,
                                )
                            }
                        }
                        WarnMode.Ban -> {
                            transaction {
                                insertBanLog(user, reason, "Troy")
                                deleteWarnLog(user.id.value.toLong())
                            }
                            message.getGuild().ban(user.id) {
                                this.reason = reason
                            }
                            message.channel.createEmbed {
                                setupBannedEmbed(
                                    user.mention,
                                    reason,
                                    "<@${kordClient.selfId.asString}>",
                                    kordClient
                                )
                            }
                        }
                        WarnMode.None -> {
                        }
                    }
                } else {
                    message.channel.createEmbed {
                        setupWarningEmbed(
                            user.mention,
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
                val user = arguments.user
                val userSnowflake = user.id.value.toLong()
                val warnReason = arguments.reason
                val moderator = "${member?.asUser()?.username}#${member?.asUser()?.discriminator}"
                val guildSnowflake = guild?.asGuild()?.id?.value?.toLong() ?: 0
                var didMaxWarningExceeded = false
                var userWarnings = 0
                var guildMaxWarnings = 0
                var guildWarnMode: WarnMode? = null

                transaction {
                    addLogger(StdOutSqlLogger)
                    SchemaUtils.create(WarnLogs)
                    insertWarnLog(user, warnReason, moderator)
                    userWarnings = getUserWarnings(userSnowflake)
                    guildMaxWarnings = getMaxWarnings(guildSnowflake)
                    guildWarnMode = getGuildWarnMode(guildSnowflake)
                    didMaxWarningExceeded = isUserWarningsExceeded(guildSnowflake, userWarnings)
                }
                respond {
                    if (didMaxWarningExceeded) {
                        val reason = "Max warnings exceeded!"
                        when (guildWarnMode) {
                            WarnMode.Kick -> {
                                transaction {
                                    insertKickLog(user, reason, "Troy")
                                    deleteWarnLog(user.id.value.toLong())
                                }
                                guild?.kick(user.id, reason)
                                embed {
                                    setupKickedEmbed(
                                        user.mention,
                                        reason,
                                        member?.mention.orEmpty(),
                                        kordClient,
                                    )
                                }
                            }
                            WarnMode.Ban -> {
                                transaction {
                                    insertBanLog(user, reason, "Troy")
                                    deleteWarnLog(user.id.value.toLong())
                                }
                                guild?.ban(user.id) {
                                    this.reason = reason
                                }
                                embed {
                                    setupBannedEmbed(
                                        user.mention,
                                        reason,
                                        member?.mention.orEmpty(),
                                        kordClient
                                    )
                                }
                            }
                            WarnMode.None -> {
                            }
                        }
                    } else {
                        embed {
                            setupWarningEmbed(
                                user.mention,
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

    companion object {
        fun deleteWarnLog(userSnowflake: Long) {
            WarnLogs.deleteWhere { WarnLogs.warnedUserId eq userSnowflake }
        }
    }

    private fun Transaction.insertWarnLog(
        user: User,
        warningReason: String,
        moderator: String
    ): InsertStatement<Number> {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(WarnLogs)
        return WarnLogs.insert {
            it[warnedUserId] = user.id.value.toLong()
            it[warnedUser] = "${user.username}#${user.discriminator}"
            it[reason] = warningReason
            it[warnedAt] = Clock.System.now().toString()
            it[warningIssuedBy] = moderator
        }
    }

    private fun getUserWarnings(userSnowflake: Long): Int {
        return WarnLogs.select { WarnLogs.warnedUserId eq userSnowflake }.count().toInt()
    }

    private fun getMaxWarnings(guildSnowflake: Long): Int {
        GuildConfig.select { GuildConfig.guildId eq guildSnowflake }.first().let {
            return it[GuildConfig.maxWarnings]
        }
    }

    private fun isUserWarningsExceeded(guildSnowflake: Long, userWarnCount: Int): Boolean {
        GuildConfig.select { GuildConfig.guildId eq guildSnowflake }.first().let {
            return it[GuildConfig.maxWarnings] <= userWarnCount
        }
    }

    private fun getGuildWarnMode(guildSnowflake: Long): WarnMode {
        GuildConfig.select { GuildConfig.guildId eq guildSnowflake }.first().let {
            return when (it[GuildConfig.warnMode]) {
                0 -> WarnMode.None
                1 -> WarnMode.Kick
                2 -> WarnMode.Ban
                else -> WarnMode.None
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
}

object WarnLogs : IntIdTable() {
    val warnedUserId = long("warnedUserId")
    val warnedUser = varchar("warnedUser", 100)
    val reason = varchar("reason", 200)
    val warnedAt = varchar("timestamp", 100)
    val warningIssuedBy = varchar("warningIssuedBy", 100)
}
