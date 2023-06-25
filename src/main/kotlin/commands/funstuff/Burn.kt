package commands.funstuff

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.Kord
import org.koin.core.component.inject
import utils.DataProvider
import utils.isGirlfriend
import utils.isOwner

class Burn : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "burn"

    class BurnArguments : Arguments() {
        val user by user {
            name = "user"
            description = "Which user do you want to light on fire?"
        }
    }

    override suspend fun setup() {
        publicSlashCommand(::BurnArguments) {
            name = "burn"
            description = "Lights fire to mentioned user."
            action {
                val burnList = DataProvider.getBurnData()
                val randomBurn = burnList[kotlin.math.floor(Math.random() * burnList.size).toInt()]
                with(arguments) {
                    when {
                        user.id.isOwner() -> {
                            respond {
                                content = "You can't hurt the god, But here's one for you.\n${member?.mention}, $randomBurn"
                            }
                            return@action
                        }

                        user.id.isGirlfriend() -> {
                            respond {
                                content =
                                    "You can't hurt her. But here is one for you. Asshole.\n${member?.mention}, $randomBurn"
                            }
                            return@action
                        }

                        user.id == kordClient.selfId -> {
                            respond {
                                content = "Huh, Burn me? But, $randomBurn"
                            }
                        }

                        else -> {
                            respond {
                                content = "${user.mention}, $randomBurn"
                            }
                        }
                    }
                }
            }
        }
    }
}
