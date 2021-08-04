package extensions

import com.kotlindiscord.kord.extensions.commands.converters.impl.booleanList
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import core.TroyExtension
import dev.kord.core.behavior.channel.createEmbed

class TestExtension : TroyExtension() {

    override val name = "test"

    class TestArguments : Arguments() {
        val string by string("string", "String argument")
        val bool by booleanList("bools", "Multiple boolean args")
    }

    override suspend fun setup() {
        troyCommand(::TestArguments) {
            name = "test"
            description = "this is a test command"
            action {
                message.channel.createEmbed {
                    title = "This is a title"
                    description = "this is embed desc"
                    field {
                        name = "String"
                        value = arguments.string
                    }
                    field {
                        name = "Booleans size ${arguments.bool.size}"
                        value = arguments.bool.joinToString(", ") { "`$it`" }
                    }
                }
            }
        }
    }
}
