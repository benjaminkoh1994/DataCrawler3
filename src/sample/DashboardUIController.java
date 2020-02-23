package sample;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import twitter4j.Twitter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.logging.Filter;

public class DashboardUIController implements Initializable {
    //-------------------------------TWITTER TABLE----------------------------------------------------------------------
    @FXML
    private TableView<TwitterTable> table;
    @FXML
    private TableColumn<TwitterTable, String> col_id;
    @FXML
    private TableColumn<TwitterTable, String> col_author;
    @FXML
    private TableColumn<TwitterTable, String> col_tweet;

    @FXML
    private TableColumn<TwitterTable, String> col_date;
    @FXML
    private TableColumn<TwitterTable, String> col_location;
    @FXML
    private TableColumn<TwitterTable, String> col_rt;
    //------------------------------------------------------------------------------------------------------------------
    //----------------------------------REDDIT TABLE--------------------------------------------------------------------
    @FXML
    private TableView<RedditTable> redditTable;
    @FXML
    private TableColumn<RedditTable, String> reddit_id;
    @FXML
    private TableColumn<RedditTable, String> reddit_author;
    @FXML
    private TableColumn<RedditTable, String> reddit_post;
    @FXML
    private TableColumn<RedditTable, String> reddit_date;
    @FXML
    private TableColumn<RedditTable, String> reddit_url;
    @FXML
    private TableColumn<RedditTable, Integer> reddit_commcount;
    //------------------------------------------------------------------------------------------------------------------
    //-------------------------------------BAR CHART--------------------------------------------------------------------
    @FXML
    private BarChart<String, Integer> barChart;
    @FXML
    private BarChart<String, Integer> redditBarChart;
    //------------------------------------------------------------------------------------------------------------------
    //-------------------------------------PIE CHART--------------------------------------------------------------------
    @FXML
    private PieChart twtPieChart;
    @FXML
    private PieChart rdtPieChart;
    //---------------------------------------GENERAL--------------------------------------------------------------------
    @FXML
    private TextField searchField;
    @FXML
    private TextField redditSearchField;
    //------------------------------------------------------------------------------------------------------------------

    //Set decimal format as 0.2f
    private static DecimalFormat df = new DecimalFormat("#.##");

    //Instantiate Observable List that is able to track any changes made to the list
    ObservableList<TwitterTable> oblist = FXCollections.observableArrayList();
    ObservableList<RedditTable> redditoblist = FXCollections.observableArrayList();

    //Action Listener to go back to the previous main menu
    public void backToMenu(ActionEvent event) throws IOException {
        //Load previous FXML
        Parent dashboard = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
        Scene dashboardScene = new Scene(dashboard, 1500, 770);

        //Delete the table data so that there will not be multiple instances of data occurring
        try {
            Connection con = DBConnector.getConnection();
            con.createStatement().executeUpdate("delete from reddit");
            con.createStatement().executeUpdate("delete from sqlite_sequence where name = 'reddit'");
            con.createStatement().executeUpdate("delete from twitter");
            con.createStatement().executeUpdate("delete from sqlite_sequence where name = 'twitter'");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        //Get stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(dashboardScene);
        window.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Initialise variables to count for sentiment values
        double tweetNegative = 0.0;
        double tweetPositive = 0.0;
        double tweetNeutral = 0.0;
        double tweettotal = 0.0;
        double redditNegative = 0.0;
        double redditPositive = 0.0;
        double redditNeutral = 0.0;
        double reddittotal = 0.0;

        // Sorting Columns
        col_rt.setSortable(true);

        //Construct a series and populate it with obserablelist data. Display in an x-y axis chart
        XYChart.Series<String,Integer> series = new XYChart.Series<>();
        XYChart.Series<String,Integer> rdtSeries = new XYChart.Series<>();

        try {
            //Connect to Database
            Connection con = DBConnector.getConnection();

            //SQL Statement to extract data from SQL Database and store as a ResultSet datatype
            ResultSet rs = con.createStatement().executeQuery("select * from twitter");
            ResultSet redrs = con.createStatement().executeQuery("select * from reddit");
            ResultSet populateTwitterBarChart = con.createStatement().executeQuery("select author, rtCount from twitter order by rtCount DESC LIMIT 5");
            ResultSet populateRedditBarChart = con.createStatement().executeQuery("select author, commCount from reddit order by commCount DESC LIMIT 5");

//-----------------------------TWITTER TABLE----------------------------------------------------------------------------
            //Go through every result and add into an oblist
            while (rs.next()){
                // initialise variables to store temporary sentiment value count
                double tweetNegativeTemp = 0.0;
                double tweetPositiveTemp = 0.0;
                double tweetNeutralTemp = 0.0;
                double tweettotalTemp = 0.0;

                oblist.add(new TwitterTable(
                        rs.getString("tid"),
                        rs.getString("author"),
                        rs.getString("tweet"),
                        rs.getString("date"),
                        rs.getString("location"),
                        rs.getInt("rtCount")
                ));
                //instantiate sentiment analyzer to analyse tweets
                sentimentAnalyzer tweetSentiment = new sentimentAnalyzer(rs.getString("tweet"));

                //count number of negative/positive sentences in that tweet as well as total number of sentences
                tweetNegativeTemp = tweetSentiment.getNegative() + tweetSentiment.getVeryNegative();
                tweetPositiveTemp = tweetSentiment.getPositive() + tweetSentiment.getVeryPositive();
                tweetNeutralTemp = tweetSentiment.getNeutral();
                tweettotalTemp = tweetNegativeTemp + tweetPositiveTemp + tweetNeutralTemp;

                //add the numbers to the variables that hold the total number of negative/positive sentences of all tweets
                tweetNegative += tweetNegativeTemp;
                tweetPositive += tweetPositiveTemp;
                tweetNeutral += tweetNeutralTemp;
                tweettotal += tweettotalTemp;
            }

//-----------------------------REDDIT TABLE----------------------------------------------------------------------------
            //Go through every result and add into an oblist
            while (redrs.next()){
                // initialise variables to store temporary sentiment value count
                double redditNegativeTemp = 0.0;
                double redditPositiveTemp = 0.0;
                double redditNeutralTemp = 0.0;
                double reddittotalTemp = 0.0;

                redditoblist.add(new RedditTable(
                        redrs.getString("pid"),
                        redrs.getString("author"),
                        redrs.getString("postTitle"),
                        redrs.getString("postDate"),
                        redrs.getString("postURL"),
                        redrs.getInt("commCount")
                ));
                //instantiate sentiment analyzer to analyse reddit posts
                sentimentAnalyzer redditSentiment = new sentimentAnalyzer(redrs.getString("postTitle"));

                //count number of negative/positive sentences in that reddit post as well as total number of sentences
                redditNegativeTemp = redditSentiment.getNegative() + redditSentiment.getVeryNegative();
                redditPositiveTemp = redditSentiment.getPositive() + redditSentiment.getVeryPositive();
                redditNeutralTemp = redditSentiment.getNeutral();
                reddittotalTemp = redditNegativeTemp + redditPositiveTemp + redditNeutralTemp;

                //add the numbers to the variables that hold the total number of negative/positive sentences of all reddit posts
                redditNegative += redditNegativeTemp;
                redditPositive += redditPositiveTemp;
                redditNeutral += redditNeutralTemp;
                reddittotal += reddittotalTemp;
            }

//--------------------------------BAR CHART TWITTER---------------------------------------------------------------------
            //Populate Twitter Bar Chart
            while(populateTwitterBarChart.next()){
                series.getData().add(new XYChart.Data(populateTwitterBarChart.getString("author"), populateTwitterBarChart.getInt("rtCount")));
            }
            barChart.getData().add(series);

//--------------------------------BAR CHART REDDIT---------------------------------------------------------------------
            //Populate Reddit Bar Chart
            while(populateRedditBarChart.next()){
                rdtSeries.getData().add(new XYChart.Data(populateRedditBarChart.getString("author"), populateRedditBarChart.getInt("commCount")));
            }
            redditBarChart.getData().add(rdtSeries);

//-----------------------------PIE CHART TWITTER-----------------------------------------------------------------------
            //Calculating into percentage and storing into double
            double twtNegativePercent = (tweetNegative/tweettotal)*100;
            double twtPositivePercent = (tweetPositive/tweettotal)*100;
            double twtNeutralPercent = (tweetNeutral/tweettotal)*100;

            //Convert to 2 Decimal Place
            String twtNeg = df.format(twtNegativePercent);
            String twtPos = df.format(twtPositivePercent);
            String twtNeu = df.format(twtNeutralPercent);

            //Populate Pie Chart
            ObservableList<PieChart.Data> twtPieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Negative: " + twtNeg + "%",Double.parseDouble(twtNeg)),
                    new PieChart.Data("Positive: " + twtPos + "%",Double.parseDouble(twtPos)),
                    new PieChart.Data("Neutral: " + twtNeu + "%",Double.parseDouble(twtNeu))
            );
            //Pie Chart Settings
            twtPieChart.setData(twtPieChartData);
            twtPieChart.setClockwise(true);
            twtPieChart.setLabelsVisible(true);

//-----------------------------PIE CHART REDDIT-----------------------------------------------------------------------
            //Calculating into percentage and storing into double
            double rdtNegativePercent = (redditNegative/reddittotal)*100;
            double rdtPositivePercent = (redditPositive/reddittotal)*100;
            double rdtNeutralPercent = (redditNeutral/reddittotal)*100;

            //Convert to 2 Decimal Place
            String rdtNeg = df.format(rdtNegativePercent);
            String rdtPos = df.format(rdtPositivePercent);
            String rdtNeu = df.format(rdtNeutralPercent);

            //Populate Pie Chart
            ObservableList<PieChart.Data> rdtPieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Negative: " + rdtNeg + "%",Double.parseDouble(rdtNeg)),
                    new PieChart.Data("Positive: " + rdtPos + "%",Double.parseDouble(rdtPos)),
                    new PieChart.Data("Neutral: " + rdtNeu + "%",Double.parseDouble(rdtNeu))
            );
            //Pie Chart Settings
            rdtPieChart.setData(rdtPieChartData);
            rdtPieChart.setClockwise(true);
            rdtPieChart.setLabelsVisible(true);

//----------------------------END OF PIE CHART -------------------------------------------------------------------------
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //match each column to the variable
        col_id.setCellValueFactory(new PropertyValueFactory<>("tid"));
        col_author.setCellValueFactory(new PropertyValueFactory<>("author"));
        col_tweet.setCellValueFactory(new PropertyValueFactory<>("tweet"));
        col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        col_location.setCellValueFactory(new PropertyValueFactory<>("location"));
        col_rt.setCellValueFactory(new PropertyValueFactory<>("rtCount"));

        reddit_id.setCellValueFactory(new PropertyValueFactory<>("pid"));
        reddit_author.setCellValueFactory(new PropertyValueFactory<>("author"));
        reddit_post.setCellValueFactory(new PropertyValueFactory<>("postTitle"));
        reddit_date.setCellValueFactory(new PropertyValueFactory<>("postDate"));
        reddit_url.setCellValueFactory(new PropertyValueFactory<>("postURL"));
        reddit_commcount.setCellValueFactory(new PropertyValueFactory<>("commCount"));

        //Populate Table
        table.setItems(oblist);
        redditTable.setItems(redditoblist);
//----------------------------------------SEARCH BAR TWITTER------------------------------------------------------------
        //Instantiate a filterable list
        FilteredList<TwitterTable> filteredTweet = new FilteredList<>(oblist, p -> true);

        //Add action listener to search for given word simultaneously as it is entered into searchfield
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredTweet.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (person.getAuthor().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (person.getTweet().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });
        //Wrap the FilteredList in a SortedList.
        SortedList<TwitterTable> sortedData = new SortedList<>(filteredTweet);

        //Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        //Add sorted (and filtered) data to the table.
        table.setItems(sortedData);
//------------------------------------------SEARCH BAR REDDIT-----------------------------------------------------------
        //Instantiate a filterable list
        FilteredList<RedditTable> filteredPosts = new FilteredList<>(redditoblist, p -> true);

        //Add action listener to search for given word simultaneously as it is entered into searchfield
        redditSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredPosts.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (person.getAuthor().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (person.getPostTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });
        //Wrap the FilteredList in a SortedList.
        SortedList<RedditTable> sortedRedditData = new SortedList<>(filteredPosts);

        //Bind the SortedList comparator to the table comparator.
        sortedRedditData.comparatorProperty().bind(redditTable.comparatorProperty());

        //Add sorted (and filtered) data to the table.
        redditTable.setItems(sortedRedditData);
    }
}
