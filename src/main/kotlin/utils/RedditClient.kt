package utils

import com.kotlindiscord.kord.extensions.utils.env
import net.dean.jraw.RedditClient
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper.automatic

object RedditClient {
    private var oauthCredentials: Credentials = Credentials.script(
        env(Environment.REDDIT_USERNAME),
        env(Environment.REDDIT_PASSWORD),
        env(Environment.REDDIT_APP_ID),
        env(Environment.REDDIT_APP_SECRET),
    )
    private var userAgent: UserAgent = UserAgent(env(Environment.REDDIT_USER_AGENT))
    private var reddit: RedditClient = automatic(OkHttpNetworkAdapter(userAgent), oauthCredentials)
    fun getClient(): RedditClient {
        return reddit
    }
}
