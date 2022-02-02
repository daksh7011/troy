package di

import data.repository.GlobalGuildConfig
import data.repository.GlobalGuildRepository
import org.litote.kmongo.coroutine.CoroutineCollection

fun provideGlobalGuildRepository(guildCollection: CoroutineCollection<GlobalGuildConfig>): GlobalGuildRepository {
    return GlobalGuildRepository(guildCollection)
}
