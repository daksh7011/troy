package di

import data.repository.BanLogsRepository
import data.repository.GlobalGuildRepository
import data.repository.KickLogsRepository
import data.repository.WarningLogsRepository
import org.litote.kmongo.coroutine.CoroutineDatabase

fun provideGlobalGuildRepository(database: CoroutineDatabase): GlobalGuildRepository {
    return GlobalGuildRepository(database.getCollection())
}

fun provideWarningLogsRepository(database: CoroutineDatabase): WarningLogsRepository {
    return WarningLogsRepository(database.getCollection())
}

fun provideKickLogsRepository(database: CoroutineDatabase): KickLogsRepository {
    return KickLogsRepository(database.getCollection())
}

fun provideBanLogsRepository(database: CoroutineDatabase): BanLogsRepository {
    return BanLogsRepository(database.getCollection())
}
