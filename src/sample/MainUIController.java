package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import twitter4j.TwitterException;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainUIController {

    //Form Validation
    @FXML
    Label warningLabel;
    @FXML
    TextArea inputTextArea;

    public void changeScene(ActionEvent event) throws IOException, TwitterException {
        boolean name = Validation.textFieldNotEmpty(inputTextArea,warningLabel,"Input Required");
        if (name) {
            String key = inputTextArea.getText().replaceAll(" ","");
            redditCrawler reddit = new redditCrawler(key);
            twitter_crawler twitter = new twitter_crawler(key);

            try {
                reddit.startCrawler();
                twitter.startCrawler();
            }
            catch (Exception ex) {
                System.out.println("Error");
                //ex.printStackTrace();
            }
            reddit.displayDataRDB();
            twitter.displayDataTDB();
            Parent dashboard = FXMLLoader.load(getClass().getResource("DashboardUI.fxml"));
            Scene dashboardScene = new Scene(dashboard);

            //get stage information
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(dashboardScene);
            window.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    try {
                        Connection con = DBConnector.getConnection();
                        con.createStatement().executeUpdate("delete from reddit");
                        con.createStatement().executeUpdate("delete from sqlite_sequence where name = 'reddit'");
                        con.createStatement().executeUpdate("delete from twitter");
                        con.createStatement().executeUpdate("delete from sqlite_sequence where name = 'twitter'");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("Closing");
                    Platform.exit();
                    System.exit(0);
                }
            });
            window.show();
        }
    }
}
