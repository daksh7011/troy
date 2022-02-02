package di

import org.koin.dsl.module

val mongoModule = module {
    single { provideMongoDatabase() }
    single { provideGlobalGuildCollection(get()) }
}

val repositoryModule = module {
    single { provideGlobalGuildRepository(get()) }
}
