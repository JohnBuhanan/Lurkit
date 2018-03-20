package johnbuhanan.com.lurkit.api;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import johnbuhanan.com.lurkit.fragments.RedditPostsFragment;
import johnbuhanan.com.lurkit.model.RedditPost;
import johnbuhanan.com.lurkit.network.VolleySingleton;

public class RedditPostsApi {
    public ArrayList<RedditPost> redditPostArrayList = new ArrayList<RedditPost>();
    private String currentAfter;
    private Context mContext;
    private static final String ALL_PAGE_URL = "https://www.reddit.com/r/all";

    public RedditPostsApi(Context context) {
        mContext = context;
    }

    public ArrayList<RedditPost> refreshRedditPosts() {
        return refreshRedditPosts(null);
    }

    public ArrayList<RedditPost> refreshRedditPosts(RedditPostsFragment.OnRedditPostsLoadedListener onRedditPostsLoadedListener) {
        currentAfter = null;
        redditPostArrayList.clear();

        return getNextRedditPosts(onRedditPostsLoadedListener);
    }

    public ArrayList<RedditPost> getNextRedditPosts() {
        return getNextRedditPosts(null);
    }

    public ArrayList<RedditPost> getNextRedditPosts(final RedditPostsFragment.OnRedditPostsLoadedListener onRedditPostsLoadedListener) {

        Uri.Builder builder = Uri.parse(ALL_PAGE_URL).buildUpon();
        builder.appendPath(".json").appendQueryParameter("raw_json", "1");

        if (!TextUtils.isEmpty(currentAfter))
            builder.appendQueryParameter("after", currentAfter);

        String url = builder.build().toString();

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray childrenArray = response.getJSONObject("data").getJSONArray("children");

                    for (int i = 0; i < childrenArray.length(); i++) {
                        JSONObject dataJsonObject = childrenArray.getJSONObject(i).getJSONObject("data");
                        RedditPost post = new RedditPost();

                        String title = dataJsonObject.getString("title");
                        String url = dataJsonObject.getString("url");

                        String thumbnail = dataJsonObject.getString("thumbnail");
                        if (!thumbnail.equals("self")) { //self post
                            if (dataJsonObject.has("preview")) {
                                JSONArray jsonArray = dataJsonObject.getJSONObject("preview")
                                        .getJSONArray("images")
                                        .getJSONObject(0)
                                        .getJSONArray("resolutions");

                                int arrayLength = jsonArray.length();
                                int highestResolutionIndex = arrayLength - 1; // highest resolution
                                if (highestResolutionIndex > 2)
                                    highestResolutionIndex = 2; // Keep it average quality.

                                thumbnail = jsonArray // TODO Array Index out of bounds error
                                        .getJSONObject(highestResolutionIndex)
                                        .getString("url");
                            }
                        }

                        currentAfter = response.getJSONObject("data").getString("after");

                        String author = dataJsonObject.getString("author");
                        String subreddit = dataJsonObject.getString("subreddit");
                        String domain = dataJsonObject.getString("domain");
                        String permalink = dataJsonObject.getString("permalink");
                        String selfttext_html = dataJsonObject.getString("selftext_html");

                        int score = dataJsonObject.getInt("score");
                        int comments = dataJsonObject.getInt("num_comments");
                        long time = dataJsonObject.getInt("created_utc");

                        //set the data for each post
                        post.setThumbnail(thumbnail);
                        post.setUrl(url);
                        post.setTitle(title);
                        post.setPermalink(permalink);
                        post.setAuthor(author);
                        post.setSubreddit(subreddit);
                        post.setDomain(domain);
                        post.setTime(time);
                        post.setScore(score);
                        post.setComments(comments);
                        post.setSelftext_html(selfttext_html);

                        redditPostArrayList.add(post);
                    }

                    if (onRedditPostsLoadedListener != null) {
                        onRedditPostsLoadedListener.onRedditPostsLoaded(redditPostArrayList);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);

        return redditPostArrayList;
    }
}