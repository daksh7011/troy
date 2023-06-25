package commands.funstuff

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand

class Bullshit : Extension() {

    override val name: String
        get() = "bullshit"

    override suspend fun setup() {
        chatCommand {
            name = "bullshit"
            description = "When someone says something shitty reply with it a bulseet."
            action {
                channel.createMessage("It a Bulseet :poop:")
            }
        }
    }
}
