package commands.`fun`

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import org.koin.core.component.inject
import utils.DataProvider
import utils.Extensions.isGirlfriend
import utils.Extensions.isOwner

@OptIn(KordPreview::class)
class Burn : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "burn"

    class BurnArguments : Arguments() {
        val user by user("user", "Which user do you want to light on fire?")
    }

    class BurnSlashArguments : Arguments() {
        val user by user("user", "Which user do you want to light on fire?")
    }

    override suspend fun setup() {
        chatCommand(::BurnArguments) {
            name = "burn"
            description = "Lights fire to mentioned user."
            action {
                val burnList = DataProvider.getBurnData()
                val randomBurn = burnList[kotlin.math.floor(Math.random() * burnList.size).toInt()]

                with(arguments) {
                    if (user.id.isOwner()) {
                        message.channel.createMessage("You can't hurt the god, But here's one for you.")
                        message.respond(randomBurn)
                        return@action
                    } else if (user.id.isGirlfriend()) {
                        message.channel.createMessage("You can't hurt her. But here is one for you. Asshole.")
                        message.respond(randomBurn)
                        return@action
                    } else if (user.id == message.kord.selfId) {
                        message.channel.createMessage("Huh, Burn me? But, $randomBurn")
                    } else {
                        message.channel.createMessage("${user.mention}, $randomBurn")
                    }
                }
            }
        }

        publicSlashCommand(::BurnSlashArguments) {
            name = "burn"
            description = "Lights fire to mentioned user."
            action {
                val burnList = DataProvider.getBurnData()
                val randomBurn = burnList[kotlin.math.floor(Math.random() * burnList.size).toInt()]
                with(arguments) {
                    if (user.id.isOwner()) {
                        respond {
                            content = "You can't hurt the god, But here's one for you.\n${member?.mention}, $randomBurn"
                        }
                        return@action
                    } else if (user.id.isGirlfriend()) {
                        respond {
                            content =
                                "You can't hurt her. But here is one for you. Asshole.\n${member?.mention}, $randomBurn"
                        }
                        return@action
                    } else if (user.id == kordClient.selfId) {
                        respond {
                            content = "Huh, Burn me? But, $randomBurn"
                        }
                    } else {
                        respond {
                            content = "${user.mention}, $randomBurn"
                        }
                    }
                }
            }
        }
    }
}
