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

	private static final String CONSUMER_KEY = "puwJYKr63IJ1nbWUxojPUvDIF";
	private static final String CONSUMER_SECRET = "LiliTjUMEdA0uFAKZlwmVEqJppRJOFk1xh4rD6gJT1QaMTs6tn";

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

			System.out.println(homeController);

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
