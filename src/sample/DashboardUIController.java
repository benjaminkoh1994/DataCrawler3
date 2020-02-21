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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
    @FXML
    private BarChart<String, Integer> barChart;
    @FXML
    private BarChart<String, Integer> redditBarChart;
    @FXML
    private TextField searchField;
    @FXML
    private TextField redditSearchField;

    ObservableList<TwitterTable> oblist = FXCollections.observableArrayList();
    ObservableList<RedditTable> redditoblist = FXCollections.observableArrayList();

    public void backToMenu(ActionEvent event) throws IOException {
        Parent dashboard = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
        Scene dashboardScene = new Scene(dashboard, 1500, 770);
        try {
            Connection con = DBConnector.getConnection();
            con.createStatement().executeUpdate("delete from reddit");
            con.createStatement().executeUpdate("delete from sqlite_sequence where name = 'reddit'");
            con.createStatement().executeUpdate("delete from twitter");
            con.createStatement().executeUpdate("delete from sqlite_sequence where name = 'twitter'");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //get stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(dashboardScene);
        window.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Sorting Columns
        col_rt.setSortable(true);

        XYChart.Series<String,Integer> series = new XYChart.Series<>();
        XYChart.Series<String,Integer> rdtSeries = new XYChart.Series<>();

        try {
            Connection con = DBConnector.getConnection();
            ResultSet rs = con.createStatement().executeQuery("select * from twitter");
            ResultSet redrs = con.createStatement().executeQuery("select * from reddit");

            ResultSet sentimentrs = con.createStatement().executeQuery("select tweet from twitter");


            ResultSet populateTwitterBarChart = con.createStatement().executeQuery("select author, rtCount from twitter order by rtCount DESC LIMIT 5");
            ResultSet populateRedditBarChart = con.createStatement().executeQuery("select author, commCount from reddit order by commCount DESC LIMIT 5");
/*
            ArrayList<String> tweets=new ArrayList<>();
            int j = 0;
            while (sentimentrs.next()) {
                tweets.add(sentimentrs.getString("tweet"));
                j++;
            }
            //System.out.println(tweets);
            //for (int i  = 0; i < j; i++) {
            sentimentAnalyzer sa = new sentimentAnalyzer(tweets.toString());
            //}
            System.out.println("Very Positive: " + sa.getVeryPositive());
            System.out.println("Positive: " + sa.getPositive());
            System.out.println("Neutral: " + sa.getNeutral());
            System.out.println("Negative: " + sa.getNegative());
            System.out.println("Very Negative: " + sa.getVeryNegative());
*/
            //Populate table twitter
            while (rs.next()){
                oblist.add(new TwitterTable(
                        rs.getString("tid"),
                        rs.getString("author"),
                        rs.getString("tweet"),
                        rs.getString("date"),
                        rs.getString("location"),
                        rs.getInt("rtCount")
                ));
            }
            //Populate table reddit
            while (redrs.next()){
                redditoblist.add(new RedditTable(
                        redrs.getString("pid"),
                        redrs.getString("author"),
                        redrs.getString("postTitle"),
                        redrs.getString("postDate"),
                        redrs.getString("postURL"),
                        redrs.getInt("commCount")
                ));
            }

            //Populate Twitter Bar Chart
            while(populateTwitterBarChart.next()){
                series.getData().add(new XYChart.Data(populateTwitterBarChart.getString("author"), populateTwitterBarChart.getInt("rtCount")));
            }
            barChart.getData().add(series);

            //Populate Reddit Bar Chart
            while(populateRedditBarChart.next()){
                rdtSeries.getData().add(new XYChart.Data(populateRedditBarChart.getString("author"), populateRedditBarChart.getInt("commCount")));
            }
            redditBarChart.getData().add(rdtSeries);

        } catch (SQLException e) {
            e.printStackTrace();
        }

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

        table.setItems(oblist);
        redditTable.setItems(redditoblist);
//----------------------------------------SEARCH BAR TWITTER------------------------------------------------------------
        FilteredList<TwitterTable> filteredTweet = new FilteredList<>(oblist, p -> true);
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
        // 3. Wrap the FilteredList in a SortedList.
        SortedList<TwitterTable> sortedData = new SortedList<>(filteredTweet);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        table.setItems(sortedData);
//------------------------------------------SEARCH BAR REDDIT-----------------------------------------------------------
        FilteredList<RedditTable> filteredPosts = new FilteredList<>(redditoblist, p -> true);
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
        // 3. Wrap the FilteredList in a SortedList.
        SortedList<RedditTable> sortedRedditData = new SortedList<>(filteredPosts);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedRedditData.comparatorProperty().bind(redditTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        redditTable.setItems(sortedRedditData);
    }

    public void onSort(SortEvent<TableView<RedditTable>> tableViewSortEvent) {

    }// onSort
}
