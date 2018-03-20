package johnbuhanan.com.lurkit;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class ContentType {
    /**
     * Checks if {@code host} is contains by any of the provided {@code bases}
     * <p/>
     * For example "www.youtube.com" contains "youtube.com" but not "notyoutube.com" or
     * "youtube.co.uk"
     *
     * @param host  A hostname from e.g. {@link URI#getHost()}
     * @param bases Any number of hostnames to compare against {@code host}
     * @return If {@code host} contains any of {@code bases}
     */
    public static boolean hostContains(String host, String... bases) {
        if (host == null || host.isEmpty()) return false;

        for (String base : bases) {
            if (base == null || base.isEmpty()) continue;

            final int index = host.lastIndexOf(base);
            if (index < 0 || index + base.length() != host.length()) continue;
            if (base.length() == host.length() || host.charAt(index - 1) == '.') return true;
        }

        return false;
    }

    public static boolean isGif(URI uri) {
        try {
            final String host = uri.getHost().toLowerCase(Locale.ENGLISH);
            final String path = uri.getPath().toLowerCase(Locale.ENGLISH);

            return hostContains(host, "gfycat.com")
                    || hostContains(host, "v.redd.it")
                    || path.endsWith(".gif")
                    || path.endsWith(".gifv")
                    || path.endsWith(".webm")
                    || path.endsWith(".mp4");

        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean isGifLoadInstantly(URI uri) {
        try {
            final String host = uri.getHost().toLowerCase(Locale.ENGLISH);
            final String path = uri.getPath().toLowerCase(Locale.ENGLISH);

            return hostContains(host, "gfycat.com") || hostContains(host, "v.redd.it") || (
                    hostContains(host, "imgur.com")
                            && (path.endsWith(".gif") || path.endsWith(".gifv") || path.endsWith(
                            ".webm"))) || path.endsWith(".mp4");

        } catch (NullPointerException e) {
            return false;
        }
    }


    public static boolean isImage(URI uri) {
        try {
            final String host = uri.getHost().toLowerCase(Locale.ENGLISH);
            final String path = uri.getPath().toLowerCase(Locale.ENGLISH);

            return host.equals("i.reddituploads.com") || path.endsWith(".png") || path.endsWith(
                    ".jpg") || path.endsWith(".jpeg");

        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean isAlbum(URI uri) {
        try {
            final String host = uri.getHost().toLowerCase(Locale.ENGLISH);
            final String path = uri.getPath().toLowerCase(Locale.ENGLISH);

            return hostContains(host, "imgur.com", "bildgur.de") && (path.startsWith("/a/")
                    || path.startsWith("/gallery/")
                    || path.startsWith("/g/")
                    || path.contains(","));

        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean isVideo(URI uri) {
        try {
            final String host = uri.getHost().toLowerCase(Locale.ENGLISH);
            final String path = uri.getPath().toLowerCase(Locale.ENGLISH);

            return hostContains(host, "youtu.be", "youtube.com",
                    "youtube.co.uk") && !path.contains("/user/") && !path.contains("/channel/");

        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean isImgurLink(String url) {
        try {
            final URI uri = new URI(url);
            final String host = uri.getHost().toLowerCase(Locale.ENGLISH);

            return hostContains(host, "imgur.com", "bildgur.de")
                    && !isAlbum(uri)
                    && !isGif(uri)
                    && !isImage(uri);

        } catch (URISyntaxException | NullPointerException e) {
            return false;
        }
    }

    /**
     * Attempt to determine the content type of a link from the URL
     *
     * @param url URL to get ContentType from
     * @return ContentType of the URL
     */
    public static Type getContentType(String url) {
        if (!url.startsWith("//") && ((url.startsWith("/") && url.length() < 4) || url.startsWith(
                "#spoiler") || url.startsWith("/spoiler") || url.startsWith("#s-") || url.equals(
                "#s") || url.equals("#ln") || url.equals("#b") || url.equals("#sp"))) {
            return Type.SPOILER;
        }

        if (url.startsWith("mailto:")) {
            return Type.EXTERNAL;
        }

        if (url.startsWith("//")) url = "https:" + url;
        if (url.startsWith("/")) url = "reddit.com" + url;
        if (!url.contains("://")) url = "http://" + url;

        try {
            final URI uri = new URI(url);
            final String host = uri.getHost().toLowerCase(Locale.ENGLISH);
            final String scheme = uri.getScheme().toLowerCase(Locale.ENGLISH);

            if (hostContains(host, "v.redd.it") || (host.equals("reddit.com") && url.contains("reddit.com/video/"))) {
                if (url.contains("DASH_")) {
                    return Type.VREDDIT_DIRECT;
                } else {
                    return Type.VREDDIT_REDIRECT;
                }
            }

            if (!scheme.equals("http") && !scheme.equals("https")) {
                return Type.EXTERNAL;
            }
            if (isVideo(uri)) {
                return Type.VIDEO;
            }
            if (isGif(uri)) {
                return Type.GIF;
            }
            if (isImage(uri)) {
                return Type.IMAGE;
            }
            if (isAlbum(uri)) {
                return Type.ALBUM;
            }
            if (hostContains(host, "imgur.com", "bildgur.de")) {
                return Type.IMGUR;
            }
            if (hostContains(host, "xkcd.com") && !hostContains("imgs.xkcd.com") && !hostContains(
                    "what-if.xkcd.com")) {
                return Type.XKCD;
            }
            if (hostContains(host, "tumblr.com") && uri.getPath().contains("post")) {
                return Type.TUMBLR;
            }
            if (hostContains(host, "reddit.com", "redd.it")) {
                return Type.REDDIT;
            }
            if (hostContains(host, "vid.me")) {
                return Type.VID_ME;
            }
            if (hostContains(host, "deviantart.com")) {
                return Type.DEVIANTART;
            }
            if (hostContains(host, "streamable.com")) {
                return Type.STREAMABLE;
            }

            return Type.LINK;

        } catch (URISyntaxException | NullPointerException e) {
            if (e.getMessage() != null && (e.getMessage().contains("Illegal character in fragment")
                    || e.getMessage().contains("Illegal character in query")
                    || e.getMessage()
                    .contains(
                            "Illegal character in path"))) //a valid link but something un-encoded in the URL
            {
                return Type.LINK;
            }
            e.printStackTrace();
            return Type.NONE;
        }
    }

    public static boolean displayImage(Type t) {
        switch (t) {

            case ALBUM:
            case DEVIANTART:
            case IMAGE:
            case XKCD:
            case TUMBLR:
            case IMGUR:
            case SELF:
                return true;
            default:
                return false;

        }
    }

    public static boolean fullImage(Type t) {
        switch (t) {

            case ALBUM:
            case DEVIANTART:
            case GIF:
            case IMAGE:
            case IMGUR:
            case STREAMABLE:
            case TUMBLR:
            case XKCD:
            case VIDEO:
            case SELF:
            case VREDDIT_DIRECT:
            case VREDDIT_REDIRECT:
            case VID_ME:
                return true;

            case EMBEDDED:
            case EXTERNAL:
            case LINK:
            case NONE:
            case REDDIT:
            case SPOILER:
            default:
                return false;

        }
    }

    public static boolean mediaType(Type t) {
        switch (t) {
            case ALBUM:
            case DEVIANTART:
            case GIF:
            case IMAGE:
            case TUMBLR:
            case XKCD:
            case IMGUR:
            case VREDDIT_DIRECT:
            case VREDDIT_REDIRECT:
            case STREAMABLE:
            case VID_ME:
                return true;
            default:
                return false;

        }
    }

    public static boolean isImgurImage(String lqUrl) {
        try {
            final URI uri = new URI(lqUrl);
            final String host = uri.getHost().toLowerCase(Locale.ENGLISH);
            final String path = uri.getPath().toLowerCase(Locale.ENGLISH);

            return (host.contains("imgur.com") || host.contains("bildgur.de")) && ((path.endsWith(
                    ".png") || path.endsWith(".jpg") || path.endsWith(".jpeg")));

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isImgurHash(String lqUrl) {
        try {
            final URI uri = new URI(lqUrl);
            final String host = uri.getHost().toLowerCase(Locale.ENGLISH);
            final String path = uri.getPath().toLowerCase(Locale.ENGLISH);

            return (host.contains("imgur.com")) && (!(path.endsWith(".png") && !path.endsWith(
                    ".jpg") && !path.endsWith(".jpeg")));

        } catch (Exception e) {
            return false;
        }
    }

    public static String formatGifUrl(String s) {
        if (s.endsWith("v") && !s.contains("streamable.com")) {
            s = s.substring(0, s.length() - 1);
        } else if (s.contains("gfycat") && (!s.contains("mp4") && !s.contains("webm"))) {
            if (s.contains("-size_restricted")) {
                s = s.replace("-size_restricted", "");
            }

            // https://gfycat.com/cajax/get/ShamelessOrderlyIndianspinyloach
            // https://thumbs.gfycat.com/ShamelessOrderlyIndianspinyloach-mobile.mp4
            s = s.replace("gfycat", "thumbs.gfycat");
            s = s + "-mobile.mp4";
        }
        if ((s.contains(".webm") || s.contains(".gif")) && !s.contains(".gifv") && s.contains(
                "imgur.com")) {
            s = s.replace(".gif", ".mp4");
            s = s.replace(".webm", ".mp4");
        }
        if (s.endsWith("/")) s = s.substring(0, s.length() - 1);
        if (s.endsWith("?r")) s = s.substring(0, s.length() - 2);
        if (s.contains("v.redd.it") && !s.contains("DASH")) {
            if (s.endsWith("/")) {
                s = s.substring(s.length() - 2);
            }
            s = s + "/DASH_9_6_M";
        }

        return s;
    }

    public enum VideoType {
        IMGUR, VID_ME, STREAMABLE, GFYCAT, DIRECT, OTHER, VREDDIT;

        public boolean shouldLoadPreview() {
            return this == OTHER;
        }
    }

    public static VideoType getVideoType(String url) {
        if (url.contains("v.redd.it")) {
            return VideoType.VREDDIT;
        }
        if (url.contains(".mp4") || url.contains("webm") || url.contains("redditmedia.com")) {
            return VideoType.DIRECT;
        }
        if (url.contains("gfycat") && !url.contains("mp4")) return VideoType.GFYCAT;
        if (url.contains("imgur.com")) return VideoType.IMGUR;
        if (url.contains("vid.me")) return VideoType.VID_ME;
        if (url.contains("streamable.com")) return VideoType.STREAMABLE;
        return VideoType.OTHER;
    }

    public enum Type {
        ALBUM, DEVIANTART, EMBEDDED, EXTERNAL, GIF, VREDDIT_DIRECT, VREDDIT_REDIRECT, IMAGE, IMGUR, LINK, NONE, REDDIT, SELF, SPOILER, STREAMABLE, VIDEO, XKCD, TUMBLR, VID_ME
    }
}
