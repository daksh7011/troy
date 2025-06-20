package troy

import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.Message
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
import troy.utils.buildFormattedDomainList
import troy.utils.containsBs
import troy.utils.containsF
import troy.utils.containsNigga
import troy.utils.containsTableFlip
import troy.utils.extractLinksFromMessage
import troy.utils.getEmbedFooter
import troy.utils.isNotBot

/**
 * Check for phishing domains in a message and send a warning if found.
 */
private suspend fun checkForPhishingDomains(message: Message, domainList: Collection<String>) {
    val listOfDomainsInMessage = message.content.extractLinksFromMessage()
    val intersectList = domainList.intersect(listOfDomainsInMessage.toSet())

    if (intersectList.isNotEmpty()) {
        val descriptionOfEmbed = "$PHISHING_WARNING_PREFIX${PHISHING_WARNING_TEXT.bold()}$PHISHING_WARNING_SUFFIX"
        val listOfBlacklistDomains = intersectList.buildFormattedDomainList()

        message.channel.createEmbed {
            title = PHISHING_WARNING_TITLE
            color = DISCORD_RED
            description = "$descriptionOfEmbed\n$listOfBlacklistDomains"
            field {
                name = PHISHING_SENDER_FIELD
                value = message.author?.mention.orEmpty()
                inline = true
            }
            footer = message.getEmbedFooter()
            timestamp = Clock.System.now()
        }
    }
}

suspend fun main() {
    val troy = getTroy()
    val kordClient: Kord = troy.getKoin().get()
    val domainList = PhishingDomainsHelper.fetchDomains()

    troy.on<MessageCreateEvent> {
        // Only process messages from non-bot users
        if (message.isNotBot()) {
            // Handle special message responses
            when {
                message.containsF() -> message.channel.createMessage(RESPONSE_F)
                message.containsNigga() -> message.channel.createMessage(RESPONSE_NIGGA)
                message.containsTableFlip() -> message.channel.createMessage(RESPONSE_TABLE_FLIP)
                message.containsBs() -> message.channel.createMessage(RESPONSE_BS)
            }

            // Check for phishing domains
            checkForPhishingDomains(message, domainList)
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

// Response messages
private const val RESPONSE_F = "f"
private const val RESPONSE_NIGGA = "Why do I have to remind you everytime?, Its aggiN"
private const val RESPONSE_TABLE_FLIP = "┬─┬ ノ( ゜-゜ノ)"
private const val RESPONSE_BS = "It a Bulseet :poop:"

// Phishing warning messages
private const val PHISHING_WARNING_TITLE = "Warning"
private const val PHISHING_WARNING_PREFIX = "There is phishing website in the message.\n\n"
private const val PHISHING_WARNING_TEXT = "Do NOT open it. Stay away from it. You have been warned.\n\n"
private const val PHISHING_WARNING_SUFFIX = "Detected malicious domains:"
private const val PHISHING_SENDER_FIELD = "Message sent by"
