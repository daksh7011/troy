package data.models

data class GlobalGuildConfig(
    val guildId: String,
    val warnMode: Int = WarningMode.Kick().warningMode,
    val maxWarnings: Int = 3,
    val inviteLink: String = ""
)

sealed class WarningMode(val warningMode: Int) {
    class None : WarningMode(0)
    class Kick : WarningMode(1)
    class Ban : WarningMode(2)
}
