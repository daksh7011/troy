package troy.core

import dev.kord.common.entity.PresenceStatus
import dev.kordex.core.DISCORD_GREEN
import dev.kordex.core.ExtensibleBot
import dev.kordex.core.utils.env
import dev.kordex.core.utils.getKoin
import troy.commands.config.InviteLink
import troy.commands.funstuff.Burn
import troy.commands.funstuff.Dictionary
import troy.commands.funstuff.Doggo
import troy.commands.funstuff.Emoji
import troy.commands.funstuff.Fact
import troy.commands.funstuff.Flip
import troy.commands.funstuff.ItsOur
import troy.commands.funstuff.Poll
import troy.commands.funstuff.Pun
import troy.commands.funstuff.SarcasticCatNo
import troy.commands.funstuff.Sike
import troy.commands.funstuff.SorryDidi
import troy.commands.funstuff.Tereko
import troy.commands.funstuff.Understandable
import troy.commands.funstuff.UrbanDictionary
import troy.commands.misc.Avatar
import troy.commands.misc.InviteTroy
import troy.commands.misc.Repo
import troy.commands.misc.Steam
import troy.commands.mod.Ban
import troy.commands.mod.Kick
import troy.commands.mod.Prune
import troy.commands.mod.Reboot
import troy.commands.mod.ResetWarnings
import troy.commands.mod.Warn
import troy.commands.nsfw.Rule34
import troy.di.mongoModule
import troy.di.repositoryModule
import troy.utils.Environment

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
            add(::Emoji)
            add(::SarcasticCatNo)
            add(::Sike)
            add(::SorryDidi)
            add(::Tereko)
            add(::UrbanDictionary)
            add(::Ban)
            add(::Kick)
            add(::InviteTroy)
            add(::InviteLink)
            add(::Dictionary)
            add(::Fact)
            add(::Steam)
//            add(::Nudes)
            add(::Rule34)
            add(::Warn)
            add(::ResetWarnings)
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
