package troy.utils

import org.koin.core.component.KoinComponent

object PhishingDomainsHelper : KoinComponent {

    fun fetchDomains(): List<String> = emptyList()

//    private const val DOMAIN_URL = "https://technowolf.in/phishingDomains"

    /*suspend fun fetchDomains(): List<String> {
        return httpClient.requestAndCatchResponse(
            identifier = "fetchPhishingDomains",
            block = { get(DOMAIN_URL).body<PhishingDomainModel>().domains },
            logPrefix = "Failed to fetch phishing domains"
        ) ?: emptyList()
    }*/
}
