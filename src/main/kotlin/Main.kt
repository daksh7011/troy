import com.kotlindiscord.kord.extensions.ExtensibleBot
import dev.kord.common.entity.PresenceStatus
import extensions.CreditsExtension
import extensions.TestExtension

suspend fun main() {
    val bot = ExtensibleBot("ODcxODM2ODY5NDkzNjYxNzM2.YQhHWw.xjVBmvqBGnQm8k9tz8ae7hlxasI") {
        messageCommands {
            defaultPrefix = "!"
            invokeOnMention = true
        }
        slashCommands {
            enabled = true
        }
        extensions {
            add(::TestExtension)
            add(::CreditsExtension)
        }
        presence {
            status = PresenceStatus.Online
            playing("with anime tiddies!")
        }
    }
    bot.start()
// perm id 260382391511
}
