package commands.`fun`

import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import com.kotlindiscord.kord.extensions.utils.respond
import core.Credits
import core.TroyExtension
import dev.kord.common.annotation.KordPreview
import utils.DataProvider
import utils.Extensions.isOwner
import java.lang.Math.floor

@OptIn(KordPreview::class)
class Burn : TroyExtension() {

    override val name: String
        get() = "burn"

    class BurnArguments : Arguments() {
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
                with(arguments) {
                    val burnList = DataProvider.getBurnData()
                    val randomBurn = burnList[floor(Math.random() * burnList.size).toInt()]
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
    }
}
