package core

import com.kotlindiscord.kord.extensions.DISCORD_GREEN
import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import commands.`fun`.Burn
import commands.misc.Credits
import dev.kord.common.entity.PresenceStatus
import dev.kord.gateway.PrivilegedIntent
import utils.Environment

@OptIn(PrivilegedIntent::class)
suspend fun getTroy(): ExtensibleBot {
    val troy = ExtensibleBot(env(Environment.TOKEN).orEmpty()) {
        messageCommands {
            defaultPrefix = "!"
            invokeOnMention = true
        }
        slashCommands {
            enabled = true
        }
        extensions {
            help {
                pingInReply = true
                color { DISCORD_GREEN }
                deletePaginatorOnTimeout = true
                deleteInvocationOnPaginatorTimeout = true
            }
            add(::Credits)
            add(::Burn)
        }
        presence {
            status = PresenceStatus.Online
            watching("Troy burn in JS")
        }
        members {
            fillPresences = true
            all()
        }
        hooks {
            created {
                println("ExtensibleBot object created, but not yet set up.")
            }
        }
    }
    return troy
}
