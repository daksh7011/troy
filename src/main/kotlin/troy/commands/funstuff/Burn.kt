package troy.commands.funstuff

import dev.kord.core.Kord
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.user
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import org.koin.core.component.inject
import troy.utils.DataProvider
import troy.utils.isGirlfriend
import troy.utils.isOwner
import kotlin.random.Random

class Burn : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "burn"

    class BurnArguments : Arguments() {
        val user by user {
            name = "user".toKey()
            description = "Which user do you want to light on fire?".toKey()
        }
    }

    override suspend fun setup() {
        publicSlashCommand(Burn::BurnArguments) {
            name = "burn".toKey()
            description = "Lights fire to mentioned user.".toKey()
            action {
                val burnList = DataProvider.getBurnData()
                val randomBurn = burnList[Random.nextInt(burnList.size)]
                with(arguments) {
                    when {
                        user.id.isOwner() -> {
                            respond {
                                content = "${CANT_HURT_GOD_MESSAGE}${member?.mention}, $randomBurn"
                            }
                            return@action
                        }
                        user.id.isGirlfriend() -> {
                            respond {
                                content = "${CANT_HURT_HER_MESSAGE}${member?.mention}, $randomBurn"
                            }
                            return@action
                        }
                        user.id == kordClient.selfId -> respond {
                            content = "${BURN_ME_MESSAGE}$randomBurn"
                        }
                        else -> respond {
                            content = "${user.mention}, $randomBurn"
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val CANT_HURT_GOD_MESSAGE = "You can't hurt the god, But here's one for you.\n"
        private const val CANT_HURT_HER_MESSAGE = "You can't hurt her. But here is one for you. Asshole.\n"
        private const val BURN_ME_MESSAGE = "Huh, Burn me? But, "
    }
}
