package commands.mod

import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import commands.mod.Warn.Companion.deleteWarnLog
import dev.kord.common.entity.Permission
import dev.kord.core.Kord
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.inject
import utils.Extensions

class ResetWarnings : Extension() {

    val kordClient: Kord by inject()

    override val name: String
        get() = "reset-warnings"

    inner class WarnResetArguments : Arguments() {
        val user by user("user", "Which user do you want to reset warnings for?")
    }

    override suspend fun setup() {
        Extensions.connectToDatabase()
        publicSlashCommand(::WarnResetArguments) {
            name = "reset-warnings"
            description = "Resets warnings for mentioned user."
            check {
                hasPermission(Permission.Administrator)
                requireBotPermissions(Permission.KickMembers)
                requireBotPermissions(Permission.BanMembers)
            }
            action {
                val user = arguments.user
                val userSnowflake = user.id.value.toLong()
                transaction {
                    addLogger(StdOutSqlLogger)
                    deleteWarnLog(userSnowflake)
                }
                respond { content = "Warnings have been reset for ${user.mention} by moderator ${member?.mention}" }
            }
        }
    }
}
