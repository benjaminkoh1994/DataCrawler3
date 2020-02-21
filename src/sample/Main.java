package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import twitter4j.TwitterException;

import java.sql.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
        Scene startScene = new Scene(root, 1500, 770);
        //second scene should be width 1500, height 770
        primaryStage.setTitle("Kerawl");
        primaryStage.setScene(startScene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("/sample/img/icon.png"));
        primaryStage.show();
}

    public static void main(String[] args) throws TwitterException {
/*
        String key = "Singapore";
        redditCrawler reddit = new redditCrawler(key);
        twitter_crawler twitter = new twitter_crawler(key);

        reddit.startCrawler();
        twitter.startCrawler();
        reddit.displayDataRDB();
        twitter.displayDataTDB();*/
        launch(args); //launches UI
    }
}
