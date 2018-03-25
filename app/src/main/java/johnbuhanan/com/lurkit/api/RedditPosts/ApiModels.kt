package johnbuhanan.com.lurkit.api.RedditPosts


/**
 * Created by john on 3/20/18.
 */
class RedditNewsResponse(val data: RedditDataResponse)

class RedditDataResponse(
        val children: List<RedditChildrenResponse>,
        val after: String?,
        val before: String?
)

class RedditChildrenResponse(val data: RedditNewsDataResponse)

class RedditNewsDataResponse(
        val author: String,
        val title: String,
        val num_comments: Int,
        val created: Long,
        val thumbnail: String,
        val url: String,
        val subreddit: String,
        val selftext_html: String,
        val score: Int,
        val domain: String,
        val preview: RedditPreviewResponse,
        val permalink: String
)

class RedditPreviewResponse(val images: List<RedditImagesResponse>)

class RedditImagesResponse(val resolutions: List<RedditImagesResolutionsResponse>)

class RedditImagesResolutionsResponse(val url: String)