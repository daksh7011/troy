package data.repository

import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class GlobalGuildRepository(private val guildCollection: CoroutineCollection<GlobalGuildConfig>) {
    suspend fun insertGlobalGuildConfig(guildId: String) {
        if (checkIfConfigExistsForGuild(guildId).not()) {
            guildCollection.insertOne(GlobalGuildConfig(guildId))
        }
    }

    suspend fun checkIfConfigExistsForGuild(guildId: String): Boolean {
        guildCollection.findOne(GlobalGuildConfig::guildId eq guildId).let {
            return it != null
        }
    }

    suspend fun getGlobalConfigForGuild(guildId: String): GlobalGuildConfig? {
        return guildCollection.findOne(GlobalGuildConfig::guildId eq guildId)
    }

    suspend fun updateInviteLinkForGuild(guildId: String, inviteLink: String) {
        if (checkIfConfigExistsForGuild(guildId).not()) return
        guildCollection.updateOne(
            GlobalGuildConfig::guildId eq guildId,
            setValue(GlobalGuildConfig::inviteLink, inviteLink)
        )
    }

    suspend fun updateWarningModeForGuild(guildId: String, warnMode: WarningMode) {
        if (checkIfConfigExistsForGuild(guildId).not()) return
        guildCollection.updateOne(
            GlobalGuildConfig::guildId eq guildId,
            setValue(GlobalGuildConfig::warnMode, warnMode.warningMode)
        )
    }

    suspend fun updateMaxWarningForGuild(guildId: String, maxWarnings: Int) {
        if (checkIfConfigExistsForGuild(guildId).not()) return
        guildCollection.updateOne(
            GlobalGuildConfig::guildId eq guildId,
            setValue(GlobalGuildConfig::maxWarnings, maxWarnings)
        )
    }
}

data class GlobalGuildConfig(
    val guildId: String,
    val warnMode: Int = WarningMode.Kick().warningMode,
    val maxWarnings: Int = 3,
    val inviteLink: String = "",
)

sealed class WarningMode(val warningMode: Int) {
    class None : WarningMode(0)
    class Kick : WarningMode(1)
    class Ban : WarningMode(2)
}
