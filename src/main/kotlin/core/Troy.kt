package core

import com.kotlindiscord.kord.extensions.DISCORD_GREEN
import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import commands.`fun`.*
import commands.misc.Avatar
import commands.misc.Repo
import commands.misc.Steam
import commands.mod.Prune
import commands.mod.Reboot
import dev.kord.gateway.PrivilegedIntent
import utils.Environment

@OptIn(PrivilegedIntent::class)
suspend fun getTroy(): ExtensibleBot {
    val troy = ExtensibleBot(env(Environment.TOKEN)) {
        chatCommands {
            defaultPrefix = env(Environment.PREFIX)
            enabled = true
            invokeOnMention = true
        }
        extensions {
            help {
                pingInReply = true
                color { DISCORD_GREEN }
                deletePaginatorOnTimeout = true
                deleteInvocationOnPaginatorTimeout = true
            }
            if (env(Environment.SENTRY_DSN).isEmpty().not()) {
                sentry {
                    enable = true
                    dsn = env(Environment.SENTRY_DSN)
                    debug = env(Environment.IS_DEBUG) == "true"
                }
            }
            add(::Burn)
            add(::Doggo)
            add(::Flip)
            add(::ItsOur)
            add(::Poll)
            add(::Prune)
            add(::Understandable)
            add(::Reboot)
            add(::Avatar)
            add(::Repo)
            add(::Steam)
        }
        presence {
            playing("/help")
        }
        members {
            fillPresences = true
            all()
        }
    }
    return troy
}
