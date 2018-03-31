package johnbuhanan.com.lurkit.api.RedditPosts

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by john on 3/20/18.
 */
interface RedditPostsAPI {
    @GET("/top.json")
    fun getTop(@Query("after") after: String, @Query("limit") limit: String): Call<RedditPostsResponse>
}