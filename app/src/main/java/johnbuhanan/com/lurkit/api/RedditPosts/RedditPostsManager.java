package johnbuhanan.com.lurkit.api.RedditPosts;

import java.util.ArrayList;
import java.util.List;

import johnbuhanan.com.lurkit.fragments.RedditPostsFragment;
import johnbuhanan.com.lurkit.model.RedditPost;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RedditPostsManager implements Callback<RedditPostsResponse> {
    public ArrayList<RedditPost> redditPostArrayList = new ArrayList<RedditPost>();
    private String currentAfter;

    private RedditPostsFragment.OnRedditPostsLoadedListener mOnRedditPostsLoadedListener;

    public RedditPostsManager(RedditPostsFragment.OnRedditPostsLoadedListener onRedditPostsLoadedListener) {
        mOnRedditPostsLoadedListener = onRedditPostsLoadedListener;
    }

    public void refreshRedditPosts() {
        currentAfter = "";
        redditPostArrayList.clear();

        getNextRedditPosts();
    }

    public void getNextRedditPosts() {
        new RedditPostsAPIClient().getNews(currentAfter, "25").enqueue(this);
    }

    @Override
    public void onResponse(Call<RedditPostsResponse> call, Response<RedditPostsResponse> response) {
        ArrayList<RedditPost> redditPosts = new ArrayList<RedditPost>();

        RedditPostsResponse redditNewsResponse = response.body();
        RedditPostsResponseData redditDataResponse = redditNewsResponse.getData();
        currentAfter = redditDataResponse.getAfter();

        List<Children> redditChildrenResponses = redditDataResponse.getChildren();

        for (Children rcr : redditChildrenResponses) {
            ChildrenData rndr = rcr.getData();

            RedditPost redditPost = new RedditPost();

            String thumbnail = rndr.getThumbnail();

            Preview redditPreviewResponse = rndr.getPreview();
            if (redditPreviewResponse != null) {
                List<Image> redditImagesResponses = redditPreviewResponse.getImages();
                Image redditImagesResponse = redditImagesResponses.get(0);
                List<Resolutions> redditImagesResolutionsResponses = redditImagesResponse.getResolutions();
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
    public void onFailure(Call<RedditPostsResponse> call, Throwable t) {

    }
}