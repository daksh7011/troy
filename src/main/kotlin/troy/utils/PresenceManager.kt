package troy.utils

import dev.kord.core.Kord
import dev.kord.gateway.builder.PresenceBuilder
import dev.kordex.core.utils.scheduling.Scheduler
import kotlinx.coroutines.flow.count
import org.koin.core.component.KoinComponent
import kotlin.math.floor

object PresenceManager : KoinComponent {

    private val scheduler = Scheduler()
    private const val REFRESH_TIMEOUT = 60L

    suspend fun setPresence(kord: Kord) {
        callScheduler(kord)
    }

    private suspend fun callScheduler(kord: Kord) {
        val listOfPresence = getPresenceList(kord)
        scheduler.schedule(
            REFRESH_TIMEOUT,
            callback = {
                kord.editPresence(listOfPresence[floor(Math.random() * listOfPresence.size).toInt()])
                callScheduler(kord)
            },
            name = "Presence Task",
        )
    }

    private suspend fun getPresenceList(kord: Kord): List<PresenceBuilder.() -> Unit> {
        val listOfPresence = mutableListOf<PresenceBuilder.() -> Unit>()
        val totalServers = kord.guilds.count()
        var totalChannels = 0
        kord.guilds.collect {
            totalChannels += it.channels.count()
        }
        listOfPresence.add { watching("$totalServers servers") }
        listOfPresence.add { watching("$totalChannels channels") }
        listOfPresence.add { playing("COD: Warzone") }
        listOfPresence.add { playing("with your heart") }
        listOfPresence.add { watching("Kratos kill Zeus") }
        listOfPresence.add { watching("NSFW channels") }
        listOfPresence.add { watching("over the walls") }
        listOfPresence.add { playing("in Erebus") }
        listOfPresence.add { playing("with Kronos") }
        listOfPresence.add { playing("in Slothie's CPU") }
        listOfPresence.add { playing("with my dark saber") }
        listOfPresence.add { watching("Helen dance") }
        listOfPresence.add { listening("to bullshit in voice") }
        listOfPresence.add { streaming("noods from NSFW", "https://slothiesmooth.dev") }
        listOfPresence.add { playing("with Lord Moti") }
        listOfPresence.add { watching("Ram Mandir construction") }
        listOfPresence.add { playing("/help") }
        return listOfPresence
    }

    fun shutdown() {
        scheduler.shutdown()
    }
}
