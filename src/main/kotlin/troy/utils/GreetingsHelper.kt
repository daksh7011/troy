package troy.utils

import dev.kord.core.Kord
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kordex.core.utils.env
import dev.kordex.core.utils.scheduling.Scheduler
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import troy.apiModels.OpenWeatherModel
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.time.Duration.Companion.hours
import kotlin.time.toKotlinDuration

// TODO: 28-Oct-21 Add dynamic subscription for this via bot command for particular guilds.
object GreetingsHelper : KoinComponent {
    private val scheduler = Scheduler()
    private val REFRESH_TIMEOUT = 6.hours

    suspend fun scheduleRecurringGreetingsCall(kord: Kord) {
        scheduler.schedule(
            REFRESH_TIMEOUT,
            callback = {
                pollForAyodhyaWeather(kord)
                scheduleRecurringGreetingsCall(kord)
            },
            name = "Greetings Scheduler",
        )
    }

    private suspend fun pollForAyodhyaWeather(kord: Kord) {
        val ayodhyaWeatherUrl =
            "https://api.openweathermap.org/data/2.5/weather?q=Ajodhya&appid=${env(Environment.OPEN_WEATHER_API_KEY)}"
        val ayodhyaWeather = requestForWeather(ayodhyaWeatherUrl)
        ayodhyaWeather?.let {
            scheduleGreetings(it, kord)
        }
    }

    private suspend fun requestForWeather(
        ayodhyaWeatherUrl: String
    ): OpenWeatherModel? {
        return httpClient.requestAndCatchResponse(
            identifier = "pollForAyodhyaWeather",
            block = { get(ayodhyaWeatherUrl).body() },
            logPrefix = "Failed to fetch weather data"
        )
    }

    private suspend fun scheduleGreetings(ayodhyaWeather: OpenWeatherModel, kord: Kord) {
        val technoTrojansGuild =
            kord.guilds.filter { it.id == getTestGuildSnowflake() }.first().asGuildOrNull()
        val chitChatsChannel =
            technoTrojansGuild.channels.filter { it.name == "bot-spam" }.first().asChannel()
        val channelBehaviour = MessageChannelBehavior(chitChatsChannel.id, kord)
        ayodhyaWeather.let {
            val morningDate =
                Instant.ofEpochMilli(it.sys.sunrise.toLong()).atZone(ZoneId.of("UTC")).toLocalDateTime()
            val morningDateDelay = Duration.between(LocalDateTime.now(), morningDate)
            val nightDate =
                Instant.ofEpochMilli(it.sys.sunset.toLong()).atZone(ZoneId.of("UTC")).toLocalDateTime()
            val nightDateDelay = Duration.between(LocalDateTime.now(), nightDate)
            Scheduler().schedule(
                delay = morningDateDelay.toKotlinDuration(),
                callback = {
                    channelBehaviour.createMessage("Good Morning! Jay Shree Ram")
                },
                name = "Morning Greetings",
            )
            Scheduler().schedule(
                delay = nightDateDelay.toKotlinDuration(),
                callback = {
                    channelBehaviour.createMessage("Good Night! Jay Shree Ram")
                },
                name = "Evening Greetings",
            )
        }
    }
}
