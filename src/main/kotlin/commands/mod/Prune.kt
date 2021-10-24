package commands.mod

import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import dev.kord.common.entity.Permission
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take

class Prune : Extension() {

    override val name: String
        get() = "prune"

    class PurgeArguments : Arguments() {
        val amount by string("number", "How many messages you want to purge?")
    }

    override suspend fun setup() {
        chatCommand(::PurgeArguments) {
            name = "purge"
            aliases = arrayOf("purge", "bulk", "clean")
            check {
                hasPermission(Permission.Administrator)
                requireBotPermissions(Permission.ManageMessages)
            }
            action {
                val amount = (arguments.amount.toIntOrNull() ?: 0)
                channel.getMessagesBefore(message.id).take(amount).collect { it.delete() }
                message.delete()
                message.channel.createMessage("Purged $amount messages.")
            }
        }
    }
}
