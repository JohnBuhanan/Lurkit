package johnbuhanan.com.lurkit.api.Comments;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.ToJson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import johnbuhanan.com.lurkit.activities.DetailsActivity;
import johnbuhanan.com.lurkit.model.Comment;
import johnbuhanan.com.lurkit.network.VolleySingleton;
import retrofit2.Call;
import retrofit2.Callback;

public class CommentsManager {

    Context mContext;
    String mPermalink;
    DetailsActivity.OnCommentsLoadedListener mOnResponseListener;

    private static final String BASE_COMMENT_URL = "https://www.reddit.com";

    public CommentsManager(Context context, String permalink, DetailsActivity.OnCommentsLoadedListener onResponseListener) {
        mContext = context;
        mPermalink = permalink;
        mOnResponseListener = onResponseListener;
    }

    public void fetchComments() {
        final ArrayList<Comment> comments = new ArrayList<Comment>();

        String url = BASE_COMMENT_URL + mPermalink + ".json?" + "raw_json=1";

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONArray childrenArray = response.getJSONObject(1).getJSONObject("data").getJSONArray("children");
                    processRecursively(comments, childrenArray, 0);
                    mOnResponseListener.onCommentsLoaded(comments);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonArrayRequest);
    }

    private void processRecursively(ArrayList<Comment> comments, JSONArray c, int level) throws Exception {
        for (int i = 0; i < c.length(); i++) {
            if (c.getJSONObject(i).optString("kind") == null)
                continue;
            if (c.getJSONObject(i).optString("kind").equals("t1") == false)
                continue;

            JSONObject data = c.getJSONObject(i).getJSONObject("data");
            Comment comment = loadComment(data, level);

            if (comment.getComment_author() != null) {
                comments.add(comment);
                addReplies(comments, data, level + 1);
            }
        }
    }

    // Load various details about the comment
    private Comment loadComment(JSONObject data, int level) {
        Comment comment = new Comment();

        try {
            comment.setComment_body(data.getString("body_html"));
            comment.setComment_author(data.getString("author"));
            comment.setComment_score(data.getInt("score"));
            comment.setComment_time(data.getLong("created_utc"));
            comment.setLevel(level);
        } catch (Exception e) {
            Log.d("ERROR", "Unable to parse comment : " + e);
        }

        return comment;
    }

    // Add replies to the comments
    private void addReplies(ArrayList<Comment> comments, JSONObject parent, int level) {
        try {
            if (parent.get("replies").equals("")) {
                // This means the comment has no replies
                return;
            }
            JSONArray r = parent.getJSONObject("replies")
                    .getJSONObject("data")
                    .getJSONArray("children");
            processRecursively(comments, r, level);
        } catch (Exception e) {
            Log.d("ERROR", "addReplies : " + e);
        }
    }
}