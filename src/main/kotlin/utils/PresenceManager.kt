package utils

import com.kotlindiscord.kord.extensions.utils.scheduling.Scheduler
import dev.kord.core.Kord
import dev.kord.gateway.builder.PresenceBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.floor
import kotlin.time.ExperimentalTime

object PresenceManager : KoinComponent {

    private val kordClient: Kord by inject()

    @OptIn(ExperimentalTime::class)
    suspend fun setPresence() {
        callScheduler()
    }

    private suspend fun callScheduler() {
        val listOfPresence = getPresenceList()
        Scheduler().schedule(
            60L,
            callback = {
                kordClient.editPresence(listOfPresence[floor(Math.random() * listOfPresence.size).toInt()])
                callScheduler()
            },
            name = "Presence Task"
        )
    }

    private suspend fun getPresenceList(): List<PresenceBuilder.() -> Unit> {
        val listOfPresence = mutableListOf<PresenceBuilder.() -> Unit>()
        val totalServers = kordClient.guilds.count()
        var totalChannels = 0
        kordClient.guilds.collect {
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
}
