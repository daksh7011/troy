package troy.di

import org.koin.dsl.module

val mongoModule = module {
    single { provideMongoDatabase() }
}

val repositoryModule = module {
    single { provideGlobalGuildRepository(get()) }
    single { provideWarningLogsRepository(get()) }
    single { provideKickLogsRepository(get()) }
    single { provideBanLogsRepository(get()) }
}
