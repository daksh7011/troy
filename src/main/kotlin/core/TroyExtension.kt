package core

import com.kotlindiscord.kord.extensions.annotations.ExtensionDSL
import com.kotlindiscord.kord.extensions.commands.MessageCommand
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import com.kotlindiscord.kord.extensions.extensions.Extension
import org.koin.core.component.KoinComponent

abstract class TroyExtension : Extension(), KoinComponent {

    @ExtensionDSL
    suspend fun <T : Arguments> troyCommand(
        arguments: (() -> T)?,
        body: suspend BaseMessageCommand<T>.() -> Unit
    ): MessageCommand<T> {
        val commandObj = BaseMessageCommand(this, arguments)
        body.invoke(commandObj)

        return command(commandObj)
    }
}