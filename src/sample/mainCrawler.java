package sample;

import twitter4j.TwitterException;

import java.util.Date;

public abstract class mainCrawler {
    protected String userKeyword;
    protected String author;
    protected String post;
    protected String url;
    protected Date date;

    //default constructor
    public mainCrawler(String userKeyword){
        this.userKeyword = userKeyword;
    }

    public abstract void startCrawler() throws TwitterException;
    public abstract String getAuthor();
    public abstract String getPost();
    public abstract String getURL();
    public abstract Date getDate();
    public abstract void displayData();
}

