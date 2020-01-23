package main.resources.application;

import main.resources.application.TwitterScraper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class MainApp extends Application {
	final private String ip = "localhost";
	final private int port = 3306;
	final private String DB_NAME = "tweetsanalysis";
	final private String DB_USER = "root";

	private static final String CONSUMER_KEY = "your-key";
	private static final String CONSUMER_SECRET = "your-secret";
	private static final String ACCESS_TOKEN = "your-token";
	private static final String ACCESS_TOKEN_SECRET = "your-secret-token";
	
	public static final TwitterScraper twitterScraper = new TwitterScraper(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN,
			ACCESS_TOKEN_SECRET);

	private Stage primaryStage;
	public static SQLManager manager;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("TweetQuake");
		manager = new SQLManager(ip, port, DB_NAME, DB_USER);
		initLayout();
	}

	public void initLayout() {
		try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Home.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("css/DarkTheme.css").toExternalForm());
			primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("Images/AppIcon.png")));
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
