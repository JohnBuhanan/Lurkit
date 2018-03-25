package johnbuhanan.com.lurkit.api.RedditPosts

import retrofit2.Call

interface NewsAPI {
    fun getNews(after: String, limit: String): Call<RedditNewsResponse>
}