package troy.commands.mod

import dev.kord.common.entity.Permission
import dev.kordex.core.checks.hasPermission
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.chatCommand
import dev.kordex.core.i18n.toKey
import kotlinx.coroutines.flow.take

class Prune : Extension() {

    override val name: String
        get() = "prune"

    inner class PurgeArguments : Arguments() {
        val amount by string {
            name = "number".toKey()
            description = "How many messages you want to purge?".toKey()
        }
    }

    override suspend fun setup() {
        chatCommand(::PurgeArguments) {
            name = "purge".toKey()
            check {
                hasPermission(Permission.Administrator)
                requireBotPermissions(Permission.ManageMessages)
            }
            action {
                val amount = arguments.amount.toIntOrNull() ?: 0
                channel.getMessagesBefore(message.id).take(amount).collect { it.delete() }
                message.delete()
                message.channel.createMessage("${PURGE_SUCCESS_MESSAGE_PREFIX}$amount messages.")
            }
        }
    }

    companion object {
        private const val PURGE_SUCCESS_MESSAGE_PREFIX = "Purged "
    }
}
