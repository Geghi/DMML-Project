package main.resources.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class MainApp extends Application {
	final private String ip = "localhost";
	final private int port = 3306;
	final private String DB_NAME = "tweetsanalysis";
	final private String DB_USER = "root";

	private Stage primaryStage;
	public static SQLManager manager;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("SentimentAnalysis");
		manager = new SQLManager(ip, port, DB_NAME, DB_USER);
		initLayout();
	}

	public void initLayout() {
		try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Home.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("css/DarkTheme.css").toExternalForm());
//			primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("Images/AppIcon.png")));
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
