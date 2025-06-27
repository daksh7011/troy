package troy.utils

import dev.kordex.core.utils.env
import net.dean.jraw.RedditClient
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper.automatic

/**
 * Singleton object that provides access to the Reddit API through JRAW.
 *
 * This client handles authentication with Reddit using the credentials
 * provided in environment variables. It creates and maintains a single
 * RedditClient instance for the application to use.
 */
object RedditClient {
    private var oauthCredentials: Credentials = Credentials.script(
        env(Environment.REDDIT_USERNAME),
        env(Environment.REDDIT_PASSWORD),
        env(Environment.REDDIT_APP_ID),
        env(Environment.REDDIT_APP_SECRET),
    )
    private var userAgent: UserAgent = UserAgent(env(Environment.REDDIT_USER_AGENT))
    private var reddit: RedditClient = automatic(OkHttpNetworkAdapter(userAgent), oauthCredentials)
    fun getClient(): RedditClient = reddit
}
