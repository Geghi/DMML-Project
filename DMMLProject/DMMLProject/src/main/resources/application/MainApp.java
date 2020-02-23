package main.resources.application;

import main.resources.HomeController;
import main.resources.application.TwitterScraper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class MainApp extends Application {
	final private String ip = "localhost";
	final private int port = 3306;
	final private String DB_NAME = "tweetquake";
	final private String DB_USER = "root";
	public static HomeController homeController;

	private static final String CONSUMER_KEY = "consumer_key";
	private static final String CONSUMER_SECRET = "consumer_secret";
	
	public static final TwitterScraper twitterScraper = new TwitterScraper(CONSUMER_KEY, CONSUMER_SECRET);
			
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
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("Home.fxml"));
			Parent root = fxmlLoader.load();
			homeController = (HomeController) fxmlLoader.getController();

			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("css/DarkTheme.css").toExternalForm());
			primaryStage.getIcons()
					.add(new Image(getClass().getClassLoader().getResourceAsStream("Images/AppIcon.png")));
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static HomeController getHomeController() {
		return homeController;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
