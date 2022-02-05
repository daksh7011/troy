package utils

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import models.PhishingDomainModel
import org.koin.core.component.KoinComponent
import org.koin.core.logger.Level

object PhishingDomainsHelper : KoinComponent {

    private const val DOMAIN_URL = "https://technowolf.in/phishingDomains"

    private val httpClient = HttpClient {
        install(JsonFeature) {
            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
        }
    }

    suspend fun fetchDomains(): List<String> {
        return httpClient.requestAndCatch({
            get<PhishingDomainModel>(DOMAIN_URL).domains
        }, {
            getKoin().logger.log(Level.ERROR, localizedMessage)
            emptyList()
        })
    }
}
