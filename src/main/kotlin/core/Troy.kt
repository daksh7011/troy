package core

import com.kotlindiscord.kord.extensions.DISCORD_GREEN
import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import commands.`fun`.Bullshit
import commands.`fun`.Burn
import commands.`fun`.Doggo
import commands.`fun`.Emoji
import commands.`fun`.Flip
import commands.`fun`.ItsOur
import commands.nsfw.Nudes
import commands.`fun`.Poll
import commands.`fun`.SarcasticCatNo
import commands.`fun`.Sike
import commands.`fun`.SorryDidi
import commands.`fun`.Tereko
import commands.`fun`.Understandable
import commands.`fun`.UrbanDictionary
import commands.misc.Avatar
import commands.misc.Invite
import commands.misc.Repo
import commands.mod.Ban
import commands.mod.Kick
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
            // add(::Steam)
            add(::Emoji)
            add(::SarcasticCatNo)
            add(::Sike)
            add(::SorryDidi)
            add(::Tereko)
            add(::UrbanDictionary)
            add(::Bullshit)
            add(::Ban)
            add(::Kick)
            add(::Invite)
            add(::Nudes)
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
