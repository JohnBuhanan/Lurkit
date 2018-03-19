package johnbuhanan.com.lurkit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import johnbuhanan.com.lurkit.R;
import johnbuhanan.com.lurkit.model.RedditPost;
import johnbuhanan.com.lurkit.viewholders.RedditCardViewHolder;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static Context mContext;
    private ArrayList<RedditPost> redditPosts;
    private View view;

    public static final int THUMBNAIL_POST_TYPE = 0;
    public static final int SELF_POST_TYPE = 1;
    public static final int LINK_POST_TYPE = 2;

//    public RecyclerViewAdapter(Context context) {
//        this.mContext = context;
//        this.redditPosts = new ArrayList<RedditPost>();
//    }

    public RecyclerViewAdapter(Context context, ArrayList<RedditPost> redditPosts) {
        this.mContext = context;
        this.redditPosts = redditPosts;
    }

    @Override
    public RedditCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout and pass to the viewholder to find each item
        view = LayoutInflater.from(mContext).inflate(R.layout.reddit_post, parent, false);

        RedditCardViewHolder viewHolder = new RedditCardViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        final RedditPost redditPost = redditPosts.get(position);

        // Either RedditCardViewHolder, SelfPostViewHolder, or LinkViewHolder?
        ((RedditCardViewHolder) viewHolder).bindViewHolder(mContext, redditPost);
    }

    public void refreshData(ArrayList<RedditPost> redditPosts) {
        this.redditPosts.clear();
        this.redditPosts = redditPosts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return redditPosts.size();
    }
}