import com.kotlindiscord.kord.extensions.commands.events.ChatCommandFailedChecksEvent
import com.kotlindiscord.kord.extensions.commands.events.ChatCommandFailedParsingEvent
import com.kotlindiscord.kord.extensions.commands.events.ChatCommandFailedWithExceptionEvent
import com.kotlindiscord.kord.extensions.commands.events.ChatCommandInvocationEvent
import com.kotlindiscord.kord.extensions.commands.events.ChatCommandSucceededEvent
import com.kotlindiscord.kord.extensions.commands.events.PublicSlashCommandFailedChecksEvent
import com.kotlindiscord.kord.extensions.commands.events.PublicSlashCommandFailedParsingEvent
import com.kotlindiscord.kord.extensions.commands.events.PublicSlashCommandFailedWithExceptionEvent
import com.kotlindiscord.kord.extensions.commands.events.PublicSlashCommandInvocationEvent
import com.kotlindiscord.kord.extensions.commands.events.PublicSlashCommandSucceededEvent
import com.kotlindiscord.kord.extensions.utils.scheduling.Scheduler
import core.getTroy
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.kordLogger
import dev.kord.gateway.PrivilegedIntent
import utils.Extensions.containsF
import utils.Extensions.containsNigga
import utils.Extensions.containsTableFlip
import utils.Extensions.isNotBot
import utils.GreetingsHelper
import utils.PresenceManager

@OptIn(PrivilegedIntent::class, kotlin.time.ExperimentalTime::class)
suspend fun main() {
    val troy = getTroy()
    troy.on<MessageCreateEvent> {
        if (message.containsF() && message.isNotBot()) {
            message.channel.createMessage("f")
        }
        if (message.containsNigga() && message.isNotBot()) {
            message.channel.createMessage("Why do I have to remind you everytime?, It's aggiN")
        }
        if (message.containsTableFlip() && message.isNotBot()) {
            message.channel.createMessage("┬─┬ ノ( ゜-゜ノ)")
        }
    }
    troy.on<ChatCommandInvocationEvent> {
        val commandName = this.command.name
        val userName = this.event.message.author?.username
        val userDiscriminator = this.event.message.author?.discriminator
        kordLogger.info("Chat Command: $commandName was triggered by $userName#$userDiscriminator")
    }
    troy.on<ChatCommandSucceededEvent> {
        kordLogger.info("${this.command.name} was successfully executed.")
    }
    troy.on<ChatCommandFailedChecksEvent> {
        val commandName = this.command.name
        kordLogger.info("Command: $commandName failed because checks did not pass.")
    }
    troy.on<ChatCommandFailedParsingEvent> {
        val commandName = this.command.name
        kordLogger.info("Command: $commandName failed because there was an parsing issue.")
    }
    troy.on<ChatCommandFailedWithExceptionEvent> {
        val commandName = this.command.name
        kordLogger.info("Command: $commandName failed because there was an exception.")
        kordLogger.info("More details about exception: ${this.throwable.stackTrace}")
    }
    troy.on<PublicSlashCommandInvocationEvent> {
        val commandName = this.command.name
        val userName = this.event.interaction.user.asUser().username
        val userDiscriminator = this.event.interaction.user.asUser().discriminator
        kordLogger.info("Slash Command: $commandName was triggered by $userName#$userDiscriminator")
    }
    troy.on<PublicSlashCommandSucceededEvent> {
        kordLogger.info("${this.command.name} was successfully executed.")
    }
    troy.on<PublicSlashCommandFailedChecksEvent> {
        val commandName = this.command.name
        kordLogger.info("SlashCommand: $commandName failed because checks did not pass.")
    }
    troy.on<PublicSlashCommandFailedParsingEvent> {
        val commandName = this.command.name
        kordLogger.info("SlashCommand: $commandName failed because there was an parsing issue.")
    }
    troy.on<PublicSlashCommandFailedWithExceptionEvent> {
        val commandName = this.command.name
        kordLogger.info("SlashCommand: $commandName failed because there was an exception.")
        kordLogger.info("More details about exception: ${this.throwable.localizedMessage}")
    }
    troy.on<ReadyEvent> {
        PresenceManager.setPresence()
        GreetingsHelper.scheduleRecurringGreetingsCall()
    }
    troy.on<DisconnectEvent> {
        Scheduler().shutdown()
    }
    troy.start()
}
