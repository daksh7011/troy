package troy

import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kordex.core.DISCORD_RED
import dev.kordex.core.commands.events.PublicSlashCommandFailedChecksEvent
import dev.kordex.core.commands.events.PublicSlashCommandFailedParsingEvent
import dev.kordex.core.commands.events.PublicSlashCommandFailedWithExceptionEvent
import dev.kordex.core.commands.events.PublicSlashCommandInvocationEvent
import dev.kordex.core.commands.events.PublicSlashCommandSucceededEvent
import dev.kordex.core.utils.env
import kotlinx.coroutines.flow.count
import kotlinx.datetime.Clock
import troy.core.getTroy
import troy.data.repository.GlobalGuildRepository
import troy.utils.Environment
import troy.utils.PhishingDomainsHelper
import troy.utils.PresenceManager
import troy.utils.bold
import troy.utils.containsBs
import troy.utils.containsF
import troy.utils.containsNigga
import troy.utils.containsTableFlip
import troy.utils.extractLinksFromMessage
import troy.utils.getEmbedFooter
import troy.utils.isNotBot
import troy.utils.italic

suspend fun main() {
    val troy = getTroy()
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
        if (message.containsBs() && message.isNotBot()) {
            message.channel.createMessage("It a Bulseet :poop:")
        }
        if (message.isNotBot()) {
            val listOfDomainsInMessage = message.content.extractLinksFromMessage()
            val intersectList = domainList.intersect(listOfDomainsInMessage.toSet())
            if (intersectList.isNotEmpty()) {
                val descriptionOfEmbed: String = "There is phishing website in the message.\n\n" +
                    "Do NOT open it. Stay away from it. You have been warned.\n\n".bold() +
                    "Detected malicious domains:"
                var listOfBlacklistDomains = ""
                intersectList.forEachIndexed { index, domain ->
                    listOfBlacklistDomains = "${index + 1}. $domain\n".italic()
                }
                message.channel.createEmbed {
                    title = "Warning"
                    color = DISCORD_RED
                    description = "$descriptionOfEmbed\n$listOfBlacklistDomains"
                    field {
                        name = "Message sent by"
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
        troy.logger.info { "Slash Command: $commandName triggered by $userName#$userDiscriminator" }
    }
    troy.on<PublicSlashCommandSucceededEvent> {
        troy.logger.info { "${this.command.name} was successfully executed." }
    }
    troy.on<PublicSlashCommandFailedChecksEvent> {
        val commandName = this.command.name
        troy.logger.info { "SlashCommand: $commandName failed because checks did not pass." }
    }
    troy.on<PublicSlashCommandFailedParsingEvent> {
        val commandName = this.command.name
        troy.logger.info { "SlashCommand: $commandName failed because there of a parsing issue." }
    }
    troy.on<PublicSlashCommandFailedWithExceptionEvent> {
        val commandName = this.command.name
        troy.logger.info { "SlashCommand: $commandName failed because there of an exception." }
        troy.logger.info { "More details about exception: ${this.throwable.localizedMessage}" }
    }
    troy.on<ReadyEvent> {
        PresenceManager.setPresence(this.kord)
//        GreetingsHelper.scheduleRecurringGreetingsCall(this.kord)
        if (env(Environment.IS_DEBUG).toBoolean().not()) {
            val stats = kordClient.guilds.count()
            troy.logger.info { "Server Count: $stats" }
        }
        val globalGuildRepository: GlobalGuildRepository = troy.getKoin().get()
        kordClient.guilds.collect { guild ->
            globalGuildRepository.insertGlobalGuildConfig(guild.id.toString())
        }
    }
    troy.on<DisconnectEvent> {
        PresenceManager.shutdown()
    }
    troy.start()
}
