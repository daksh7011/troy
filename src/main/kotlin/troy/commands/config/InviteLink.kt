package troy.commands.config

import dev.kord.common.entity.Permission
import dev.kordex.core.checks.hasPermission
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import org.koin.core.component.inject
import troy.data.repository.GlobalGuildRepository
import troy.utils.bold
import troy.utils.isEmptyOrBlank

class InviteLink : Extension() {

    override val name: String
        get() = "invite-link"

    inner class InviteLinkArgument : Arguments() {
        val inviteLink by string {
            name = "url".toKey()
            description = "Provide Permanent invite link for this server.".toKey()
        }
    }

    override suspend fun setup() {
        val globalGuildRepository: GlobalGuildRepository by inject()
        publicSlashCommand(::InviteLinkArgument) {
            name = "invite-link".toKey()
            description = "Setup invite link for this server".toKey()
            check { hasPermission(Permission.Administrator) }
            action {
                val guildId = guild?.id?.toString().orEmpty()
                val guildName = guild?.asGuild()?.name.bold()
                val doesGuildConfigExist =
                    globalGuildRepository.checkIfConfigExistsForGuild(guildId)
                val wasLinkProvidedInArguments = arguments.inviteLink.isBlank().not()

                if (wasLinkProvidedInArguments && doesGuildConfigExist) {
                    if (isProvidedLinkValid(arguments.inviteLink)) {
                        globalGuildRepository.updateInviteLinkForGuild(guildId, arguments.inviteLink)
                        respond { content = "Invite link updated for $guildName" }
                    } else {
                        respond {
                            content = "You might want to check your link. It does not look like a **Discord invite link**."
                        }
                    }
                } else {
                    globalGuildRepository.getGlobalConfigForGuild(guildId)?.let {
                        if (it.inviteLink.isEmptyOrBlank()) {
                            respond {
                                content = "No invite link has been set for $guildName.\n You can set it by executing same " +
                                    "command, but followed by **URL** of the invite link."
                            }
                        } else {
                            respond { content = "Invite link for $guildName: ${it.inviteLink}" }
                        }
                    }
                }
            }
        }
    }

    private fun isProvidedLinkValid(inviteLink: String): Boolean =
        inviteLink.matches(PATTERN_DISCORD_COM) || inviteLink.matches(PATTERN_DISCORD_GG)

    companion object {
        // Pre-compile regex patterns to avoid recompilation on each validation
        private val PATTERN_DISCORD_COM = Regex("^((https?)://)+(discord)+\\.(com)+/(invite)/.*")
        private val PATTERN_DISCORD_GG = Regex("^((https?)://)+(discord)+\\.(gg)/.*")
    }
}
