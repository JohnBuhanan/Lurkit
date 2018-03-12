package johnbuhanan.com.lurkit.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import johnbuhanan.com.lurkit.R;
import johnbuhanan.com.lurkit.model.RedditPost;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ExpandedImageView extends AppCompatActivity {

    private Toolbar mToolbar;
    private PhotoViewAttacher mPhotoViewAttacher;
    private PhotoView mPhotoView;
    private String imageUrl;
    private ProgressBar mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_expanded_image_view)
                .setSwipeBackView(R.layout.swipeback_custom);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mPhotoView = (PhotoView) findViewById(R.id.expanded_image);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Back");

        mProgressbar = (ProgressBar) findViewById(R.id.progressbar);

        Bundle bundle = getIntent().getExtras();
        RedditPost redditPost = (RedditPost) bundle.getSerializable("post");
        imageUrl = redditPost.getUrl();

        Glide.with(this).asBitmap().load(imageUrl).into(mPhotoView);
        mPhotoViewAttacher = new PhotoViewAttacher(mPhotoView);

        mPhotoViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float v, float v1) {
                finish();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        Intent intent;

        if (id == android.R.id.home) {
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        Intent intent = null;

        switch (view.getId()) {

            case R.id.action_open_browser:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(imageUrl));
                startActivity(intent);
                break;
            case R.id.action_download:
                new MyAsyncTask().execute(imageUrl);
                break;
            case R.id.action_share:
                intent = new Intent(Intent.ACTION_SEND);
                Uri comicUri = Uri.parse(imageUrl);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, comicUri.toString());
                startActivity(Intent.createChooser(intent, "Share with"));
                break;
            case R.id.action_close:
                finish();
                break;
        }
    }

    public class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        int counter = 0;

        @Override
        protected void onPreExecute() {
            mProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressbar.setProgress(values[0]);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean success = false;

            URL imageDownloadURL;
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            File folder;

            try {
                imageDownloadURL = new URL(params[0]);
                connection = (HttpURLConnection) imageDownloadURL.openConnection();
                connection.connect();
                inputStream = connection.getInputStream();
                folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +
                        "/" + Uri.parse(params[0]).getLastPathSegment());

                outputStream = new FileOutputStream(folder);

                int read = -1;

                byte[] buffer = new byte[1024];

                while ((read = inputStream.read(buffer)) != -1) {

                    if (outputStream != null) {
                        outputStream.write(buffer, 0, read);

                        while (counter < 5) {

                            SystemClock.sleep(500);
                            counter++;
                            publishProgress(counter * 20);
                        }

                        publishProgress(counter);
                    }
                }

                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (connection != null) {
                    connection.disconnect();
                }

                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressbar.setVisibility(View.GONE);
            Toast.makeText(ExpandedImageView.this, "Image Downloaded Successfully", Toast.LENGTH_LONG).show();
        }
    }
}