package commands.mod

import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import data.repository.WarningLogsRepository
import dev.kord.common.entity.Permission
import dev.kord.core.Kord
import org.koin.core.component.inject

class ResetWarnings : Extension() {

    val kordClient: Kord by inject()

    override val name: String
        get() = "reset-warnings"

    inner class WarnResetArguments : Arguments() {
        val user by user {
            name = "user"
            description = "Which user do you want to reset warnings for?"
        }
    }

    override suspend fun setup() {
        val warningLogsRepository: WarningLogsRepository by inject()
        publicSlashCommand(::WarnResetArguments) {
            name = "reset-warnings"
            description = "Resets warnings for mentioned user."
            check {
                hasPermission(Permission.Administrator)
                requireBotPermissions(Permission.KickMembers)
                requireBotPermissions(Permission.BanMembers)
            }
            action {
                warningLogsRepository.deleteWarningsForUser(arguments.user.id.toString())
                respond {
                    content = "Warnings have been reset for ${arguments.user.mention} by moderator ${member?.mention}"
                }
            }
        }
    }
}
