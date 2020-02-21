package sample;

public class TwitterTable {
    String tid, author, tweet, date, location, rtCount;

    public TwitterTable(String tid, String author, String tweet, String date, String location, String rtCount) {
        this.tid = tid;
        this.author = author;
        this.tweet = tweet;
        this.date = date;
        this.location = location;
        this.rtCount = rtCount;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRtCount() {
        return rtCount;
    }

    public void setRtCount(String rtCount) {
        this.rtCount = rtCount;
    }

    /*
    public ModelTable(String id, String post, String author) {
        this.id = id;
        this.post = post;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }*/
}
