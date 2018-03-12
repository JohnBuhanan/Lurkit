package johnbuhanan.com.lurkit.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import johnbuhanan.com.lurkit.R;
import johnbuhanan.com.lurkit.model.RedditPost;

public class YoutubeActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    public static final String API_KEY = "AIzaSyAahJ4hxag_fChMffe83o_bHDhz4nqAPWQ";

    private String youtubeUrl;
    private String normalYoutubeUrl;
    private String browserYoutubeUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_youtube)
                .setSwipeBackView(R.layout.swipeback_custom);

        Bundle bundle = getIntent().getExtras();
        RedditPost redditPost = (RedditPost) bundle.getSerializable("post");

        youtubeUrl = redditPost.getUrl();
        browserYoutubeUrl = redditPost.getUrl();

        if (youtubeUrl.contains("youtube")) {
            normalYoutubeUrl = youtubeUrl.substring(youtubeUrl.lastIndexOf("=") + 1);
        } else {
            normalYoutubeUrl = youtubeUrl.substring(youtubeUrl.lastIndexOf("/") + 1);
        }

        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        youTubePlayerView.initialize(API_KEY, this);
    }

    public void onClick(View view) {
        Intent intent = null;

        switch (view.getId()) {
            case R.id.youtube_root:
                finish();
                break;
            case R.id.action_open_browser:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(browserYoutubeUrl));
                startActivity(intent);
                break;
            case R.id.action_share:
                intent = new Intent(Intent.ACTION_SEND);
                Uri comicUri = Uri.parse(browserYoutubeUrl);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, comicUri.toString());
                startActivity(Intent.createChooser(intent, "Share with"));
                break;
            case R.id.action_close:
                finish();
                break;
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        /** Start buffering **/
        if (!b) {
            youTubePlayer.loadVideo(normalYoutubeUrl);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Failure to Initialize!", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }
}