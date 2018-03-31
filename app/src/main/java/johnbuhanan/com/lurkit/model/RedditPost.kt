package johnbuhanan.com.lurkit.model


import java.io.Serializable

class RedditPost : Serializable {
    var title: String? = null
    var url: String? = null
    var domain: String? = null
    var author: String? = null
    var subreddit: String? = null
    var selftext_html: String? = null
    var permalink: String? = null
    var thumbnail: String? = null
    var score: Int = 0
    var comments: Int = 0
    var time: Long = 0
}