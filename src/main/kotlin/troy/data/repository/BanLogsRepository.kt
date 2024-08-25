package troy.data.repository

import dev.kord.core.entity.User
import kotlinx.datetime.Clock
import org.litote.kmongo.coroutine.CoroutineCollection
import troy.data.models.BanLogs

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
