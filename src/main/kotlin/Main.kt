import com.kotlindiscord.kord.extensions.DISCORD_RED
import com.kotlindiscord.kord.extensions.commands.events.PublicSlashCommandFailedChecksEvent
import com.kotlindiscord.kord.extensions.commands.events.PublicSlashCommandFailedParsingEvent
import com.kotlindiscord.kord.extensions.commands.events.PublicSlashCommandFailedWithExceptionEvent
import com.kotlindiscord.kord.extensions.commands.events.PublicSlashCommandInvocationEvent
import com.kotlindiscord.kord.extensions.commands.events.PublicSlashCommandSucceededEvent
import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.scheduling.Scheduler
import core.getTroy
import data.repository.GlobalGuildRepository
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.kordLogger
import kotlinx.coroutines.flow.count
import kotlinx.datetime.Clock
import org.discordbots.api.client.DiscordBotListAPI
import utils.Environment
import utils.PhishingDomainsHelper
import utils.PresenceManager
import utils.containsF
import utils.containsNigga
import utils.containsTableFlip
import utils.extractLinksFromMessage
import utils.getEmbedFooter
import utils.isNotBot

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
            message.channel.createMessage("Why do I have to remind you everytime?, Its aggiN")
        }
        if (message.containsTableFlip() && message.isNotBot()) {
            message.channel.createMessage("┬─┬ ノ( ゜-゜ノ)")
        }
        if (message.isNotBot()) {
            val listOfDomainsInMessage = message.content.extractLinksFromMessage()
            val intersectList = domainList.intersect(listOfDomainsInMessage.toSet())
            if (intersectList.isNotEmpty()) {
                val descriptionOfEmbed: String = "There is phishing website in the message.\n" +
                    "Do NOT open it. Stay away from it. You have been warned.\n" +
                    "Detected malicious domains:\n"
                var listOfBlacklistDomains = ""
                intersectList.forEachIndexed { index, domain ->
                    listOfBlacklistDomains = "${index + 1}. - $domain\n"
                }
                message.channel.createEmbed {
                    title = "Warning"
                    color = DISCORD_RED
                    description = "$descriptionOfEmbed\n$listOfBlacklistDomains"
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
    troy.on<PublicSlashCommandInvocationEvent> {
        val commandName = this.command.name
        val userName = this.event.interaction.user.asUser().username
        val userDiscriminator = this.event.interaction.user.asUser().discriminator
        kordLogger.info("Slash Command: $commandName triggered by $userName#$userDiscriminator")
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
        kordLogger.info("SlashCommand: $commandName failed because there of a parsing issue.")
    }
    troy.on<PublicSlashCommandFailedWithExceptionEvent> {
        val commandName = this.command.name
        kordLogger.info("SlashCommand: $commandName failed because there of an exception.")
        kordLogger.info("More details about exception: ${this.throwable.localizedMessage}")
    }
    troy.on<ReadyEvent> {
        PresenceManager.setPresence(this.kord)
        // GreetingsHelper.scheduleRecurringGreetingsCall()
        if (env(Environment.IS_DEBUG).toBoolean().not()) {
            val stats = kordClient.guilds.count()
            kordLogger.info("Server Count: $stats")
            api.setStats(stats)
        }
        val globalGuildRepository: GlobalGuildRepository = troy.getKoin().get()
        kordClient.guilds.collect { guild ->
            globalGuildRepository.insertGlobalGuildConfig(guild.id.toString())
        }
    }
    troy.on<DisconnectEvent> {
        Scheduler().shutdown()
    }
    troy.start()
}
