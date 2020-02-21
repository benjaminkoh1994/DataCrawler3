package sample;

import net.dean.jraw.*;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.*;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;


import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

public class redditCrawler extends mainCrawler {

   public redditCrawler(String userKeyword) {
      super(userKeyword);
   }// redditCrawler

   @Override
   public void startCrawler() {
      UserAgent userAgent = new UserAgent("desktop", "com.example.script", "v0.1", "mattbdean");

      Credentials credentials = Credentials.script("firezxc4901", "1qwer$#@!",
              "L1B44_Tq3SyQYA", "jfG6SFE0h6Mf5JIMQmke6FmcT6M");

      // This is what really sends HTTP requests
      NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);

      RedditClient reddit = OAuthHelper.automatic(adapter, credentials);

//       To Crawl SUBREDDIT PAGES
      DefaultPaginator<Submission> sg = reddit.subreddit(userKeyword).posts().limit(15).timePeriod(TimePeriod.YEAR).sorting(SubredditSort.NEW).build();
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
      try{
         for (Submission s : sg.next()) {
            if (!s.isSelfPost()) {
               super.post = s.getTitle();
               super.author = s.getAuthor();
//             super.date = s.getCreated();
               String dateStr = sdf.format(s.getCreated());
               super.date = sdf.parse(dateStr);
               super.url = s.getUrl();
               int score = s.getScore();
               int count = s.getCommentCount();
               saveToRDb(super.post,super.author, dateStr, s.getId() ,s.getUrl(), s.getCommentCount(), s.getScore());
            }
         }
      }catch (ParseException pe) {
         System.out.println(pe);
      }
      /*
      catch (ApiException eapi){
         System.out.println(eapi.getExplanation());
      }
      catch (RateLimitException erate){
         System.out.println("Try again in " + erate.getCooldown());
      }
      catch (RedditException ereddit) {
         System.out.println(ereddit.getMessage());
      }
      catch (SuspendedAccountException esuspended){
         System.out.println("User '" + esuspended.getName() + "' has been suspended.");
      }*/
   }//startCrawler

   @Override
   public String getAuthor() {
      return super.author;
   }

   @Override
   public String getPost() {
      return super.post;
   }

   @Override
   public String getURL() {
      return super.url;
   }

   @Override
   public Date getDate() {
      return super.date;
   }


   public void saveToRDb(String post, String author, String date, String ID, String postUrl, int commCount, int score) {

      try {
         Connection conn = DBConnector.getConnection();

         String insertstr = "INSERT INTO reddit ('postTitle','author','postDate','ID','postUrl', 'commCount','score') VALUES (?,?,?,?,?,?,?)";

         PreparedStatement insertstmt = conn.prepareStatement(insertstr);


			insertstmt.setString(1,post);
			insertstmt.setString(2,author);
			insertstmt.setString(3,date);
			insertstmt.setString(4,ID);
			insertstmt.setString(5,postUrl);
			insertstmt.setInt(6,commCount);
			insertstmt.setInt(7,score);
         insertstmt.executeUpdate();

         // executeUpdate() for INSERT,UPDATE, DELETE
         // executeQuery() for SELECT

      }catch (SQLException sqle){
//         System.out.println(sqle);
      }
   }
   public void displayDataRDB() {
      try {
         Connection conn = DBConnector.getConnection();

         String selectStr = "SELECT * FROM reddit";

         PreparedStatement selstmt = conn.prepareStatement(selectStr);

         ResultSet rs = selstmt.executeQuery();
         while (rs.next()) {
            System.out.println(rs.getString("postTitle"));
         }
         // executeUpdate() for INSERT,UPDATE, DELETE
         // executeQuery() for SELECT
      }catch (Exception e){
         System.out.println(e);
      }
   }
   @Override
   public void displayData() {
      System.out.println("Title: "+ getPost());
      System.out.println("Author: "+ getAuthor());
      System.out.println("Date: "+ getDate());
      System.out.println("Link: "+ getURL());
   }
}// Class