package commands.`fun`

import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import com.kotlindiscord.kord.extensions.commands.slash.AutoAckType
import com.kotlindiscord.kord.extensions.utils.respond
import core.Credits
import core.TroyExtension
import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import org.koin.core.component.inject
import utils.DataProvider
import utils.Extensions.getTestGuildSnowflake
import utils.Extensions.isOwner

@OptIn(KordPreview::class)
class Burn : TroyExtension() {

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
        troyCommand(::BurnArguments) {
            name = "burn"
            description = "Lights fire to mentioned user."
            credits.add(
                Credits("Abhay Malik", "https://gitlab.com/Kingslayer47/", "Original Idea")
            )
            action {
                val burnList = DataProvider.getBurnData()
                val randomBurn = burnList[kotlin.math.floor(Math.random() * burnList.size).toInt()]

                with(arguments) {
                    if (user.id.isOwner()) {
                        message.channel.createMessage("You can't hurt the god, But here's one for you.")
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

        slashCommand(::BurnSlashArguments) {
            name = "burn"
            description = "Lights fire to mentioned user."
            autoAck = AutoAckType.PUBLIC
            guild(getTestGuildSnowflake())
            action {
                val burnList = DataProvider.getBurnData()
                val randomBurn = burnList[kotlin.math.floor(Math.random() * burnList.size).toInt()]
                with(arguments) {
                    if (user.id.isOwner()) {
                        publicFollowUp {
                            content = "You can't hurt the god, But here's one for you.\n${user.mention}, $randomBurn"
                        }
                    } else if (user.id == kordClient.selfId) {
                        publicFollowUp {
                            content = "Huh, Burn me? But, $randomBurn"
                        }
                    } else {
                        publicFollowUp {
                            content = "${user.mention}, $randomBurn"
                        }
                    }
                }
            }
        }
    }
}
