package data.repository

import data.models.BanLogs
import dev.kord.core.entity.User
import kotlinx.datetime.Clock
import org.litote.kmongo.coroutine.CoroutineCollection

class BanLogsRepository(private val bansCollection: CoroutineCollection<BanLogs>) {
    suspend fun insertBanLog(user: User, banReason: String, moderator: String) {
        bansCollection.insertOne(
            BanLogs(
                user.id.toString(),
                "${user.username}#${user.discriminator}",
                banReason,
                Clock.System.now().toString(),
                moderator,
            ),
        )
    }
}
