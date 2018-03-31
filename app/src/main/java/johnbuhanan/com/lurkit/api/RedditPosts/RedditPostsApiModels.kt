package johnbuhanan.com.lurkit.api.RedditPosts


/**
 * Created by john on 3/20/18.
 */
class RedditPostsResponse(val data: RedditPostsResponseData)

class RedditPostsResponseData(
        val children: List<Children>,
        val after: String?
)

class Children(val data: ChildrenData)

class ChildrenData(
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
        val preview: Preview,
        val permalink: String
)

class Preview(val images: List<Image>)

class Image(val resolutions: List<Resolutions>)

class Resolutions(val url: String)