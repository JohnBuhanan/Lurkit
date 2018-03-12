package johnbuhanan.com.lurkit.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import java.util.ArrayList;

import johnbuhanan.com.lurkit.api.CommentsApi;
import johnbuhanan.com.lurkit.R;
import johnbuhanan.com.lurkit.adapters.CommentAdapter;
import johnbuhanan.com.lurkit.model.Comment;
import johnbuhanan.com.lurkit.model.RedditPost;
import johnbuhanan.com.lurkit.viewholders.RedditCardViewHolder;

public class DetailsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CommentAdapter adapter;
    private ListView mListView;
    private View mHeader;

    private String url;
    private String permalink;

    public interface OnCommentsLoadedListener {
        public void onCommentsLoaded(ArrayList<Comment> commentArrayList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_details)
                .setSwipeBackView(R.layout.swipeback_custom);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Back");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mHeader = LayoutInflater.from(getApplicationContext()).inflate(R.layout.reddit_post, null);
        CardView cardView = mHeader.findViewById(R.id.card_view);

        mListView = (ListView) findViewById(R.id.comment_listView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        Bundle bundle = getIntent().getExtras();
        RedditPost redditPost = (RedditPost) bundle.getSerializable("post");
        url = redditPost.getUrl();
        permalink = redditPost.getPermalink();

        RedditCardViewHolder redditCardViewHolder = new RedditCardViewHolder(mHeader);
        redditCardViewHolder.bindViewHolder(getApplicationContext(), redditPost);
        redditCardViewHolder.mPostDetailsContainer.setOnClickListener(null);

        adapter = new CommentAdapter(getApplicationContext(), new CommentsApi(getApplicationContext(), permalink, new OnCommentsLoadedListener() {
            @Override
            public void onCommentsLoaded(ArrayList<Comment> commentArrayList) {
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                mListView.setVisibility(View.VISIBLE);
            }
        }).fetchComments());

        mListView.addHeaderView(mHeader);
        mListView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ArrayList<Comment> comments = new CommentsApi(getApplicationContext(), permalink, new DetailsActivity.OnCommentsLoadedListener() {
                    @Override
                    public void onCommentsLoaded(ArrayList<Comment> commentArrayList) {
                        adapter.clear();
                        adapter.addAll(commentArrayList);
                        adapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mListView.setVisibility(View.VISIBLE);
                    }
                }).fetchComments();

                mSwipeRefreshLayout.setRefreshing(true);
                mListView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}