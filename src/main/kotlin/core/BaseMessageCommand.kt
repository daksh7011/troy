package core

import com.kotlindiscord.kord.extensions.commands.MessageCommand
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import com.kotlindiscord.kord.extensions.extensions.Extension

class BaseMessageCommand<T : Arguments>(extension: Extension, arguments: (() -> T)? = null) :
    MessageCommand<T>(extension, arguments) {
    val credits: MutableList<Credits> = mutableListOf(Credits())
}

data class Credits(
    val name: String = "SlothieSmooth",
    val url: String = "https://gitlab.com/daksh7011",
    val reason: String = "Overall Codebase"
)
