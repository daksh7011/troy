package troy.utils

import io.ktor.client.call.body
import io.ktor.client.request.get
import org.koin.core.component.KoinComponent
import troy.apiModels.PhishingDomainModel

object PhishingDomainsHelper : KoinComponent {

    private const val DOMAIN_URL = "https://technowolf.in/phishingDomains"

    suspend fun fetchDomains(): List<String> {
        return httpClient.requestAndCatchResponse(
            block = { get(DOMAIN_URL).body<PhishingDomainModel>().domains },
            logPrefix = "Failed to fetch phishing domains"
        ) ?: emptyList()
    }
}
