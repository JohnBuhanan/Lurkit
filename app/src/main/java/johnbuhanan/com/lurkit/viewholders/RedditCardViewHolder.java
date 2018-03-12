package johnbuhanan.com.lurkit.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.URLConnection;

import johnbuhanan.com.lurkit.LaunchActivityClickListener;
import johnbuhanan.com.lurkit.R;
import johnbuhanan.com.lurkit.activities.DetailsActivity;
import johnbuhanan.com.lurkit.activities.ExpandedImageView;
import johnbuhanan.com.lurkit.activities.GifActivity;
import johnbuhanan.com.lurkit.activities.WebActivity;
import johnbuhanan.com.lurkit.activities.YoutubeActivity;
import johnbuhanan.com.lurkit.model.RedditPost;

public class RedditCardViewHolder extends RecyclerView.ViewHolder {
    private TextView mPostTitle, mPostScore, mComments, mSubreddit, mPostThumbnailLink, mPostThumbnailDecorator, mNoThumbnailSelfBodyText, mNoThumbnailSelfWebLink;
    private ImageView mPostThumbnail;
    private RelativeLayout mPostThumbnailContainer;
    public LinearLayout mPostDetailsContainer;

    public RedditCardViewHolder(final View itemView) {
        super(itemView);

        mPostTitle = (TextView) itemView.findViewById(R.id.post_title_textView);
        mPostThumbnail = (ImageView) itemView.findViewById(R.id.post_thumbnail);

        mPostThumbnailLink = (TextView) itemView.findViewById(R.id.post_thumbnail_link);
        mPostThumbnailDecorator = (TextView) itemView.findViewById(R.id.post_thumbnail_decorator);

        mNoThumbnailSelfBodyText = (TextView) itemView.findViewById(R.id.no_thumbnail_self_body_text);
        mNoThumbnailSelfWebLink = (TextView) itemView.findViewById(R.id.no_thumbnail_self_web_link);

        mPostScore = (TextView) itemView.findViewById(R.id.post_score);
        mComments = (TextView) itemView.findViewById(R.id.post_comments);
        mSubreddit = (TextView) itemView.findViewById(R.id.post_subreddit);

        mPostThumbnailContainer = (RelativeLayout) itemView.findViewById(R.id.post_thumbnail_container);
        mPostDetailsContainer = (LinearLayout) itemView.findViewById(R.id.post_details_container);
    }

    // Can we do all branching logic here? Bind everything the three types of views have in common, and then split up depending on ViewType?
    // How do we know the ViewType here?
    public void bindViewHolder(final Context context, RedditPost redditPost) {
        //get all the relevant post data and then bind it to the
        //appropriate views below
        final String title = redditPost.getTitle();
        final String subreddit = redditPost.getSubreddit();
        final String thumbnail = redditPost.getThumbnail();
        final int postScore = redditPost.getScore();
        final int comments = redditPost.getComments();

        mPostTitle.setText(title);
        mSubreddit.setText(subreddit);
        mPostScore.setText(postScore + " points");
        mComments.setText(comments + " comments");

        mPostDetailsContainer.setOnClickListener(new LaunchActivityClickListener(context, redditPost, DetailsActivity.class));

        // Turn all optional things off so they can be turned on selectively.
        mPostThumbnailContainer.setVisibility(View.GONE);
        mPostThumbnailLink.setVisibility(View.GONE);
        mPostThumbnailDecorator.setVisibility(View.GONE);
        mNoThumbnailSelfBodyText.setVisibility(View.GONE);
        mNoThumbnailSelfWebLink.setVisibility(View.GONE);

        if (thumbnail.equals("self")) {
            bindNoThumbnailSelfType(context, redditPost);
        } else if (thumbnail.equals("default")) {
            bindNoThumbnailLinkType(context, redditPost);
        } else {
            bindThumbnailType(context, redditPost);
        }
    }

    private void bindThumbnailType(Context context, RedditPost redditPost) {
        final String url = redditPost.getUrl();
        final String domain = redditPost.getDomain();
        String type = URLConnection.guessContentTypeFromName(url);
        final String thumbnail = redditPost.getThumbnail();

        mPostThumbnailContainer.setVisibility(View.VISIBLE);
        Glide.with(context).load(thumbnail).into(mPostThumbnail);

        if (type == null)
            type = "web";

        // I can't tell if an imgur resource is going to be a .jpg or .png
        boolean isImage = type.equals("image/jpeg") || type.equals("image/png");// || (url.contains("imgur") && !url.contains("\\a\\"));

        //handle images
//        "image/jpeg"
        if (url.contains(".gif") || url.contains("gfy")) { //handle gif content
            mPostThumbnailDecorator.setVisibility(View.VISIBLE);
            mPostThumbnailDecorator.setText("[Gif]");

            mPostThumbnailContainer.setOnClickListener(new LaunchActivityClickListener(context, redditPost, GifActivity.class));
            //handle youtube content
        } else if (isImage) {
            mPostThumbnailContainer.setOnClickListener(new LaunchActivityClickListener(context, redditPost, ExpandedImageView.class));
        } else if (domain.equals("youtube.com") || domain.equals("youtu.be") || domain.equals("m.youtube.com")) {
            mPostThumbnailDecorator.setVisibility(View.VISIBLE);
            mPostThumbnailDecorator.setText("[YouTube]");

            mPostThumbnailContainer.setOnClickListener(new LaunchActivityClickListener(context, redditPost, YoutubeActivity.class));
        } else {
            mPostThumbnailLink.setVisibility(View.VISIBLE);
            mPostThumbnailLink.setText(redditPost.getUrl());

            mPostThumbnailContainer.setOnClickListener(new LaunchActivityClickListener(context, redditPost, WebActivity.class));
        }
    }

    private void bindNoThumbnailLinkType(Context context, RedditPost redditPost) {
        mNoThumbnailSelfWebLink.setVisibility(View.VISIBLE);
        mNoThumbnailSelfWebLink.setText(redditPost.getUrl());
        mNoThumbnailSelfWebLink.setOnClickListener(new LaunchActivityClickListener(context, redditPost, WebActivity.class));
    }

    private void bindNoThumbnailSelfType(Context context, RedditPost redditPost) {

        String selfTextHtmlString = redditPost.getSelftext_html();
        if (selfTextHtmlString == null || selfTextHtmlString.equals("null"))
            return;

        mNoThumbnailSelfBodyText.setVisibility(View.VISIBLE);
        Spanned selfTextHtml = Html.fromHtml(selfTextHtmlString);
        mNoThumbnailSelfBodyText.setText(trimTrailingWhitespace(selfTextHtml));
        mNoThumbnailSelfBodyText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public CharSequence trimTrailingWhitespace(CharSequence source) {
        if (source == null)
            return "";

        // loop back to the first non-whitespace character
        int i = source.length();
        while (--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }

        return source.subSequence(0, i + 1);
    }
}