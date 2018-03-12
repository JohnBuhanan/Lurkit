package johnbuhanan.com.lurkit.model;


import java.io.Serializable;

public class RedditPost implements Serializable {
    private String title, url, after, domain, author, subreddit, selftext_html, permalink, thumbnail, youtubeThumbnail;
    private int score, comments;
    private long time;

    public String getThumbnail() {
        return thumbnail;
    }

    public String getYoutubeThumbnail() {
        return youtubeThumbnail;
    }

    public void setYoutubeThumbnail(String youtubeThumbnail) {
        this.youtubeThumbnail = youtubeThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getSelftext_html() {
        return selftext_html;
    }

    public void setSelftext_html(String selftext_html) {
        this.selftext_html = selftext_html;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}