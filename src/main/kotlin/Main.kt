import com.kotlindiscord.kord.extensions.DISCORD_RED
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
import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.scheduling.Scheduler
import core.getTroy
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.kordLogger
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.flow.count
import kotlinx.datetime.Clock
import org.discordbots.api.client.DiscordBotListAPI
import utils.Environment
import utils.Extensions.containsF
import utils.Extensions.containsNigga
import utils.Extensions.containsTableFlip
import utils.Extensions.getEmbedFooter
import utils.Extensions.isNotBot
import utils.PhishingDomainsHelper
import utils.PresenceManager

@OptIn(PrivilegedIntent::class, kotlin.time.ExperimentalTime::class)
suspend fun main() {
    val troy = getTroy()
    val api: DiscordBotListAPI = DiscordBotListAPI.Builder()
        .token(env(Environment.TOP_GG_TOKEN))
        .botId(env(Environment.BOT_ID))
        .build()
    val kordClient: Kord = troy.getKoin().get()
    val domainList = PhishingDomainsHelper.fetchDomains()

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
        if (message.isNotBot()) {
            domainList.filter { message.content.contains(it) }.let {
                if (it.isNotEmpty()) {
                    message.channel.createEmbed {
                        title = "Warning"
                        color = DISCORD_RED
                        description = "_${it.first()}_ is **Phishing website**. Stay away from this site. " +
                                "You have been warned!"
                        field {
                            name = "Author"
                            value = message.author?.mention.orEmpty()
                            inline = true
                        }
                        footer = message.getEmbedFooter()
                        timestamp = Clock.System.now()
                    }
                }
            }
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
        // GreetingsHelper.scheduleRecurringGreetingsCall()
        if (env(Environment.IS_DEBUG).toBoolean().not()) {
            val stats = kordClient.guilds.count()
            kordLogger.info("Server Count: $stats")
            api.setStats(stats)
        }
    }
    troy.on<DisconnectEvent> {
        Scheduler().shutdown()
    }
    troy.start()
}
