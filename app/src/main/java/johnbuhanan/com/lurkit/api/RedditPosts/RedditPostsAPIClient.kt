package johnbuhanan.com.lurkit.api.RedditPosts

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RedditPostsAPIClient() {

    private val redditApi: RedditPostsAPI

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://www.reddit.com")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        redditApi = retrofit.create(RedditPostsAPI::class.java)
    }

    fun getNews(after: String, limit: String): Call<RedditPostsResponse> {
        return redditApi.getTop(after, limit)
    }
}