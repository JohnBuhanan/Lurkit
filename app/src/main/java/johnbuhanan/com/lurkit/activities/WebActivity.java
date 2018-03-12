package johnbuhanan.com.lurkit.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import johnbuhanan.com.lurkit.R;
import johnbuhanan.com.lurkit.model.RedditPost;

public class WebActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private WebView mWebView;
    private String webUrl;
    private static final String READABILITY_PREFIX = "http://www.readability.com/m?url=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_web)
                .setSwipeBackView(R.layout.swipeback_custom);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Back");

        Bundle bundle = getIntent().getExtras();
        RedditPost redditPost = (RedditPost) bundle.getSerializable("post");
        webUrl = redditPost.getUrl();

        mWebView = (WebView) findViewById(R.id.webView);

        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        // Configure the client to use when opening URLs
        mWebView.setWebViewClient(new MyBrowser());
        // Load the initial URL
        mWebView.loadUrl(webUrl);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    // Manages the behavior when URLs are loaded
    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent;

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_goBack:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }

                return true;
            case R.id.action_goForward:
                if (mWebView.canGoForward()) {

                    mWebView.goForward();
                }

                return true;
            case R.id.action_refresh:
                mWebView.loadUrl(webUrl);

                return true;
            case R.id.action_share:
                intent = new Intent(Intent.ACTION_SEND);
                Uri comicUri = Uri.parse(webUrl);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, comicUri.toString());
                startActivity(Intent.createChooser(intent, "Share with"));

                return true;
            case R.id.action_readability:
                mWebView.loadUrl(READABILITY_PREFIX + webUrl);
                item.setChecked(true);

                return true;
            case R.id.action_desktop:
                String ua = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
                mWebView.getSettings().setUserAgentString(ua);
                mWebView.loadUrl(webUrl);
                item.setChecked(true);

                return true;
            case R.id.action_browser:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(webUrl));
                startActivity(intent);

                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}