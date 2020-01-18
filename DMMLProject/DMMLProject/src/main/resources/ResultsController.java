package main.resources;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ResultsController {
	@FXML
	private Button homeButton;

	@FXML
	public void loadHomePage(ActionEvent event) {
		try {
			System.out.println("Loading Home Page ...");
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Home.fxml"));
			Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			window.setScene(scene);
			window.show();
			System.out.println("Home Page Loaded!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}