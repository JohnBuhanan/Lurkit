package johnbuhanan.com.lurkit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import johnbuhanan.com.lurkit.model.RedditPost;

public class LaunchActivityClickListener implements View.OnClickListener {
    private RedditPost mRedditPost;
    private Class mActivityClass;
    private Context mContext;

    public LaunchActivityClickListener(Context context, RedditPost redditPost, Class activityClass) {
        mContext = context;

        mRedditPost = redditPost;
        mActivityClass = activityClass;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext, mActivityClass);

        Bundle bundle = new Bundle();

        bundle.putSerializable("post", mRedditPost);

        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }
}