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
        //Get FXML file and set it as the starting scene
        Parent root = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
        Scene startScene = new Scene(root, 1500, 770);

        //Basic Primary Scene Set Up
        primaryStage.setTitle("Kerawl");
        primaryStage.setScene(startScene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("/sample/img/icon.png"));
        primaryStage.show();
}

    public static void main(String[] args) throws TwitterException {
        launch(args); //Launches UI
    }
}
