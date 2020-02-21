package sample;

public class RedditTable {
    String pid, author, postTitle, postDate, postURL;
    Integer commCount;

    public RedditTable(String pid, String author, String postTitle, String postDate, String postURL, Integer commCount) {
        this.pid = pid;
        this.author = author;
        this.postTitle = postTitle;
        this.postDate = postDate;
        this.postURL = postURL;
        this.commCount = commCount;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostURL() {
        return postURL;
    }

    public void setPostURL(String postURL) {
        this.postURL = postURL;
    }

    public Integer getCommCount() {
        return commCount;
    }

    public void setCommCount(Integer commCount) {
        this.commCount = commCount;
    }
}
