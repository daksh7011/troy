package troy

import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * JRAW is a vendored jar without a POM, so its runtime deps (Moshi, OkHttp) are not resolved for us.
 * Runs the auth + listing flow used by the nudes command against canned responses to catch missing or
 * incompatible classes.
 */
class RedditClientLinkageTest {
    private val listingJson = javaClass.getResource("/reddit/listing.json")!!.readText()

    private val fakeReddit = Interceptor { chain ->
        val json = when (chain.request().url.encodedPath) {
            "/api/v1/access_token" -> """{"access_token":"t","token_type":"bearer","expires_in":3600,"scope":"*"}"""
            "/r/test/hot" -> listingJson
            else -> error("Unexpected request: ${chain.request().url}")
        }
        Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(json.toResponseBody("application/json".toMediaType()))
            .build()
    }

    @Test
    fun `jraw authenticates and parses subreddit posts`() {
        val http = OkHttpClient.Builder().addInterceptor(fakeReddit).build()
        val reddit = OAuthHelper.automatic(
            OkHttpNetworkAdapter(UserAgent("troy-test"), http),
            Credentials.script("user", "pass", "id", "secret"),
        )

        val posts = reddit.subreddit("test").posts().sorting(SubredditSort.HOT).limit(1).build().next()

        Assertions.assertEquals(1, posts.size)
        Assertions.assertEquals("https://i.redd.it/abc.jpg", posts.first().url)
        Assertions.assertTrue(posts.first().isNsfw)
    }
}
