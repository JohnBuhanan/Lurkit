package johnbuhanan.com.lurkit.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

import johnbuhanan.com.lurkit.ItemOffsetDecoration;
import johnbuhanan.com.lurkit.R;
import johnbuhanan.com.lurkit.adapters.RecyclerViewAdapter;
import johnbuhanan.com.lurkit.api.RedditPostsApi;
import johnbuhanan.com.lurkit.model.RedditPost;
import johnbuhanan.com.lurkit.network.VolleySingleton;

public class RedditPostsFragment extends Fragment {

    private RedditPostsApi mRedditPostsApi = new RedditPostsApi(getContext());

    private RequestQueue mRequestQueue;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter adapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private StaggeredGridLayoutManager mLayoutManager;

    //variables for scrolling down to more items
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 8;
    private int pastVisibleItems, visibleItemCount, totalItemCount;

    public interface OnRedditPostsLoadedListener {
        public void onRedditPostsLoaded(ArrayList<RedditPost> redditPostArrayList);
    }

    public RedditPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    OnRedditPostsLoadedListener onRedditPostsLoadedListener = new OnRedditPostsLoadedListener() {
        @Override
        public void onRedditPostsLoaded(ArrayList<RedditPost> redditPostArrayList) {
            adapter.notifyItemRangeChanged(0, redditPostArrayList.size());

            //handle refresh actions
            mSwipeRefreshLayout.setRefreshing(false);

            if (redditPostArrayList.size() == 25) { // Hacky way to only do animation with first load.
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
                mRecyclerView.startAnimation(animation);
            }

            mRecyclerView.setVisibility(View.VISIBLE);
        }
    };

    final SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);

            //setting up the recyclerview with the adapter
            adapter.refreshData(mRedditPostsApi.refreshRedditPosts(onRedditPostsLoadedListener));
            previousTotal = 0;
            mSwipeRefreshLayout.setRefreshing(true);
        }
    };

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            totalItemCount = mLayoutManager.getItemCount();

            int[] firstVisibleItems = null;
            firstVisibleItems = mLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);

            if (firstVisibleItems != null && firstVisibleItems.length > 0) {
                pastVisibleItems = firstVisibleItems[0];
            }

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            } else {
                if (firstVisibleItems[0] + visibleThreshold >= totalItemCount) {
                    // End has been reached
                    // Do something
                    mRedditPostsApi.getNextRedditPosts(onRedditPostsLoadedListener);

                    loading = true;
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_reddit_posts, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.all_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        //JSON request
        mRequestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        mRecyclerView.addItemDecoration(itemDecoration);

        //swipe to refresh layout
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        //keep loading additions posts
        mRecyclerView.addOnScrollListener(onScrollListener);

        adapter = new RecyclerViewAdapter(getActivity(), mRedditPostsApi.redditPostArrayList);

        mRecyclerView.setAdapter(adapter);

        onRefreshListener.onRefresh();

        return rootView;
    }

    //Handle actions in toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                onRefreshListener.onRefresh();

                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}