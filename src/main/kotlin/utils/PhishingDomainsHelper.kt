package utils

import apiModels.PhishingDomainModel
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.koin.core.component.KoinComponent
import org.koin.core.logger.Level

object PhishingDomainsHelper : KoinComponent {

    private const val DOMAIN_URL = "https://technowolf.in/phishingDomains"

    suspend fun fetchDomains(): List<String> {
        return httpClient.requestAndCatch({
            get(DOMAIN_URL).body<PhishingDomainModel>().domains
        }, {
            getKoin().logger.log(Level.ERROR, localizedMessage)
            emptyList()
        })
    }
}
