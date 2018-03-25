package johnbuhanan.com.lurkit.api.RedditPosts;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import johnbuhanan.com.lurkit.fragments.RedditPostsFragment;
import johnbuhanan.com.lurkit.model.RedditPost;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RedditPostsApi implements Callback<RedditNewsResponse> {
    public ArrayList<RedditPost> redditPostArrayList = new ArrayList<RedditPost>();
    private String currentAfter;
    private Context mContext;
    private static final String ALL_PAGE_URL = "https://www.reddit.com/r/all";
    private RedditPostsFragment.OnRedditPostsLoadedListener mOnRedditPostsLoadedListener;

    public RedditPostsApi(Context context, RedditPostsFragment.OnRedditPostsLoadedListener onRedditPostsLoadedListener) {
        mContext = context;
        mOnRedditPostsLoadedListener = onRedditPostsLoadedListener;
    }

    public void refreshRedditPosts() {
        currentAfter = "";
        redditPostArrayList.clear();

        getNextRedditPosts();
    }

    public void getNextRedditPosts() {
        new NewsRestAPI().getNews(currentAfter, "25").enqueue(this);
    }

    @Override
    public void onResponse(Call<RedditNewsResponse> call, Response<RedditNewsResponse> response) {
        ArrayList<RedditPost> redditPosts = new ArrayList<RedditPost>();

        RedditNewsResponse redditNewsResponse = response.body();
        RedditDataResponse redditDataResponse = redditNewsResponse.getData();
        currentAfter = redditDataResponse.getAfter();

        List<RedditChildrenResponse> redditChildrenResponses = redditDataResponse.getChildren();

        for (RedditChildrenResponse rcr : redditChildrenResponses) {
            RedditNewsDataResponse rndr = rcr.getData();

            RedditPost redditPost = new RedditPost();

            String thumbnail = rndr.getThumbnail();

            RedditPreviewResponse redditPreviewResponse = rndr.getPreview();
            if (redditPreviewResponse != null) {
                List<RedditImagesResponse> redditImagesResponses = redditPreviewResponse.getImages();
                RedditImagesResponse redditImagesResponse = redditImagesResponses.get(0);
                List<RedditImagesResolutionsResponse> redditImagesResolutionsResponses = redditImagesResponse.getResolutions();
                if (redditImagesResolutionsResponses.size() != 0) {
                    thumbnail = redditImagesResolutionsResponses.get(0).getUrl().trim();
                    thumbnail = thumbnail.replace("&amp;", "&");
                }
            }

            redditPost.setThumbnail(thumbnail);
            redditPost.setUrl(rndr.getUrl());
            redditPost.setTitle(rndr.getTitle());
            redditPost.setPermalink(rndr.getPermalink());
            redditPost.setAuthor(rndr.getAuthor());
            redditPost.setSubreddit(rndr.getSubreddit());
            redditPost.setDomain(rndr.getDomain());
            redditPost.setTime(rndr.getCreated());
            redditPost.setScore(rndr.getScore());
            redditPost.setComments(rndr.getNum_comments());
            redditPost.setSelftext_html(rndr.getSelftext_html());
            redditPosts.add(redditPost);
        }

        mOnRedditPostsLoadedListener.onRedditPostsLoaded(redditPosts);
    }

    @Override
    public void onFailure(Call<RedditNewsResponse> call, Throwable t) {

    }
}