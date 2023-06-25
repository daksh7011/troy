package data.repository

import data.models.WarningLogs
import dev.kord.core.entity.User
import kotlinx.datetime.Clock
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

class WarningLogsRepository(private val warningCollection: CoroutineCollection<WarningLogs>) {
    suspend fun insertUserWarning(user: User, warningReason: String, moderator: String) {
        warningCollection.insertOne(
            WarningLogs(
                user.id.toString(),
                "${user.username}#${user.discriminator}",
                warningReason,
                Clock.System.now().toString(),
                moderator
            )
        )
    }

    suspend fun getUserWarningsCount(userId: String): Int {
        return warningCollection.find(WarningLogs::warnedUserId eq userId).toList().size
    }

    fun didUserExceedWarnings(guildMaxWarnings: Int, currentWarnings: Int): Boolean {
        return guildMaxWarnings <= currentWarnings
    }

    suspend fun deleteWarningsForUser(userId: String) {
        warningCollection.deleteMany(WarningLogs::warnedUserId eq userId)
    }
}
