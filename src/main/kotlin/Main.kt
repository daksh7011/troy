import core.getTroy
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.gateway.PrivilegedIntent
import utils.Extensions.containsF
import utils.Extensions.containsNigga
import utils.Extensions.isNotBot

@OptIn(PrivilegedIntent::class)
suspend fun main() {
    val troy = getTroy()
    troy.on<MessageCreateEvent> {
        if (message.containsF() && message.isNotBot()) {
            message.channel.createMessage("f")
        }
        if (message.containsNigga() && message.isNotBot()) {
            message.channel.createMessage("Why do I have to remind you everytime?, It's aggiN")
        }
    }
    troy.start()
// perm id 260382391511
}

