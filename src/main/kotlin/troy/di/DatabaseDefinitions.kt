package troy.di

import dev.kordex.core.utils.env
import io.ktor.http.encodeURLPath
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import troy.utils.Environment

fun provideMongoDatabase(): CoroutineDatabase {
    val url = env(Environment.MONGO_URL)
    val userName = env(Environment.MONGO_USERNAME)
    val password = env(Environment.MONGO_PASSWORD)
    val queryParams = "retryWrites=true&w=majority".encodeURLPath()
    val databaseName = if (env(Environment.IS_DEBUG).toBoolean()) "Troy_Dev" else "Troy_Prod"
    val connectionString = "mongodb+srv://$userName:$password@$url/$databaseName?$queryParams"
    return KMongo.createClient(connectionString).coroutine.getDatabase(databaseName)
}
