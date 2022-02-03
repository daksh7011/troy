package data.repository

import data.models.KickLogs
import dev.kord.core.entity.User
import kotlinx.datetime.Clock
import org.litote.kmongo.coroutine.CoroutineCollection

class KickLogsRepository(private val kicksCollection: CoroutineCollection<KickLogs>) {
    suspend fun insertKickLog(user: User, kickReason: String, moderator: String) {
        kicksCollection.insertOne(
            KickLogs(
                user.id.asString,
                "${user.username}#${user.discriminator}",
                kickReason,
                Clock.System.now().toString(),
                moderator
            )
        )
    }
}
