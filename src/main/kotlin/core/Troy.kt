package core

import com.kotlindiscord.kord.extensions.DISCORD_GREEN
import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.getKoin
import commands.config.InviteLink
import commands.funstuff.Burn
import commands.funstuff.Dictionary
import commands.funstuff.Doggo
import commands.funstuff.Emoji
import commands.funstuff.Fact
import commands.funstuff.Flip
import commands.funstuff.ItsOur
import commands.funstuff.Poll
import commands.funstuff.Pun
import commands.funstuff.SarcasticCatNo
import commands.funstuff.Sike
import commands.funstuff.SorryDidi
import commands.funstuff.Tereko
import commands.funstuff.Understandable
import commands.funstuff.UrbanDictionary
import commands.misc.Avatar
import commands.misc.Invite
import commands.misc.Repo
import commands.misc.Steam
import commands.mod.Ban
import commands.mod.Kick
import commands.mod.Prune
import commands.mod.Reboot
import commands.mod.ResetWarnings
import commands.mod.Warn
import commands.nsfw.Nudes
import commands.nsfw.Rule34
import dev.kord.common.entity.PresenceStatus
import di.mongoModule
import di.repositoryModule
import utils.Environment
import utils.provideUnleashClient

suspend fun getTroy(): ExtensibleBot {
    val unleash = provideUnleashClient()
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
            add(::Emoji)
            add(::SarcasticCatNo)
            add(::Sike)
            add(::SorryDidi)
            add(::Tereko)
            add(::UrbanDictionary)
            add(::Ban)
            add(::Kick)
            add(::Invite)
            add(::InviteLink)
            add(::Dictionary)
            add(::Fact)
            if (unleash?.isEnabled("steam") != false) add(::Steam)
            if (unleash?.isEnabled("nudes") != false) {
                add(::Nudes)
                add(::Rule34)
            }
            if (unleash?.isEnabled("warn") != false) {
                add(::Warn)
                add(::ResetWarnings)
            }
            add(::Pun)
        }
        presence {
            status = PresenceStatus.Online
            playing("/help")
        }
        hooks {
            afterKoinSetup {
                registerKoinModules()
            }
        }
        members {
            fillPresences = true
            all()
        }
    }
    return troy
}

private fun registerKoinModules() {
    getKoin().loadModules(
        listOf(
            mongoModule,
            repositoryModule,
        ),
    )
}
