package troy.di

import org.litote.kmongo.coroutine.CoroutineDatabase
import troy.data.repository.BanLogsRepository
import troy.data.repository.GlobalGuildRepository
import troy.data.repository.KickLogsRepository
import troy.data.repository.WarningLogsRepository

fun provideGlobalGuildRepository(database: CoroutineDatabase): GlobalGuildRepository = GlobalGuildRepository(database.getCollection())

fun provideWarningLogsRepository(database: CoroutineDatabase): WarningLogsRepository = WarningLogsRepository(database.getCollection())

fun provideKickLogsRepository(database: CoroutineDatabase): KickLogsRepository = KickLogsRepository(database.getCollection())

fun provideBanLogsRepository(database: CoroutineDatabase): BanLogsRepository = BanLogsRepository(database.getCollection())
