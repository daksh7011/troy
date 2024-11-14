package troy.commands.mod

import dev.kord.common.entity.Permission
import dev.kord.core.Kord
import dev.kordex.core.checks.hasPermission
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.user
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import org.koin.core.component.inject
import troy.data.repository.WarningLogsRepository

class ResetWarnings : Extension() {

    val kordClient: Kord by inject()

    override val name: String
        get() = "reset-warnings"

    inner class WarnResetArguments : Arguments() {
        val user by user {
            name = "user".toKey()
            description = "Which user do you want to reset warnings for?".toKey()
        }
    }

    override suspend fun setup() {
        val warningLogsRepository: WarningLogsRepository by inject()
        publicSlashCommand(::WarnResetArguments) {
            name = "reset-warnings".toKey()
            description = "Resets warnings for mentioned user.".toKey()
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
