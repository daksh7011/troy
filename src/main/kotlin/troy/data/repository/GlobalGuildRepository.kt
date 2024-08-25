package troy.data.repository

import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import troy.data.models.GlobalGuildConfig
import troy.data.models.WarningMode
import troy.utils.orZero

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

    suspend fun getGlobalConfigForGuild(guildId: String): GlobalGuildConfig? = guildCollection.findOne(GlobalGuildConfig::guildId eq guildId)

    suspend fun updateInviteLinkForGuild(guildId: String, inviteLink: String) {
        guildCollection.updateOne(
            GlobalGuildConfig::guildId eq guildId,
            setValue(GlobalGuildConfig::inviteLink, inviteLink),
        )
    }

    suspend fun updateWarningModeForGuild(guildId: String, warnMode: WarningMode) {
        guildCollection.updateOne(
            GlobalGuildConfig::guildId eq guildId,
            setValue(GlobalGuildConfig::warnMode, warnMode.warningMode),
        )
    }

    suspend fun updateMaxWarningForGuild(guildId: String, maxWarnings: Int) {
        guildCollection.updateOne(
            GlobalGuildConfig::guildId eq guildId,
            setValue(GlobalGuildConfig::maxWarnings, maxWarnings),
        )
    }

    suspend fun getWarnModeForGuild(guildId: String): WarningMode {
        return when (guildCollection.findOne(GlobalGuildConfig::guildId eq guildId)?.warnMode) {
            1 -> WarningMode.Kick()
            2 -> WarningMode.Ban()
            else -> WarningMode.None()
        }
    }

    suspend fun getMaxWarningsForGuild(guildId: String): Int = guildCollection.findOne(GlobalGuildConfig::guildId eq guildId)?.maxWarnings.orZero()
}
