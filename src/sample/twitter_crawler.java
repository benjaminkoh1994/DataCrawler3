package sample;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class twitter_crawler extends mainCrawler{

    public twitter_crawler(String userKeyword) {
        super(userKeyword);
    }

    @Override
    public void startCrawler() throws TwitterException {
        try {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("M7AEnQQV3BtpETFHkncMKyBIg")
                    .setOAuthConsumerSecret("XJ0buE8dg7ttXkNGu40Wgcwi2xZ0dr6KRifpV3N0mVeNBDdZmX")
                    .setOAuthAccessToken("1871694931-b8KWE9v0gp26o90oqyi6ieDQaAbwz8VbzX71lGq")
                    .setOAuthAccessTokenSecret("b1ry9doEACgfnoYNs8ODzW168WMLMwXt7yTLjuodSVLjp");
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();

            Query query = new Query(this.userKeyword);
            query.setCount(100);
            QueryResult result = twitter.search(query);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            for (Status status : result.getTweets()) {
//                System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText() + " " + status.getCreatedAt() + " " + status.getUser().getLocation() + '\n');
                String username = status.getUser().getScreenName();
                String tweet = status.getText();
                String d = sdf.format(status.getCreatedAt());
                Date date = sdf.parse(d);

                saveToTDb(status.getUser().getScreenName(),status.getText(),date.toString(),status.getUser().getLocation(),status.getFavoriteCount(),status.getRetweetCount());
            }
        }catch (TwitterException te) {
            System.out.println(te);
        }catch (ParseException pe) {
            System.out.println(pe);
        }
    }

    @Override
    public String getAuthor() {
        return null;
    }

    @Override
    public String getPost() {
        return null;
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public Date getDate() {
        return null;
    }

    public static void saveToTDb(String author, String tweet, String date, String location, int favCount, int rtCount) {
        try {
            Connection conn = DBConnector.getConnection();

            String insertstr = "INSERT INTO twitter ('author','tweet','date','location','favCount', 'rtCount') VALUES (?,?,?,?,?,?)";

            PreparedStatement insertstmt = conn.prepareStatement(insertstr);


            insertstmt.setString(1,"@"+author);
            insertstmt.setString(2,tweet);
            insertstmt.setString(3,date);
            insertstmt.setString(4,location);
            insertstmt.setInt(5,favCount);
            insertstmt.setInt(6,rtCount);
            insertstmt.executeUpdate();

            // executeUpdate() for INSERT,UPDATE, DELETE
            // executeQuery() for SELECT

        }catch (SQLException sqle){
            System.out.println(sqle);
        }
    }
    public void displayDataTDB() {
        try {
            Connection conn = DBConnector.getConnection();


            String selectStr = "SELECT * FROM twitter";

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

    }
}
