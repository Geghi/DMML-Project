package main.resources;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import main.resources.application.MainApp;

public class HomeController {

	@FXML
	private Button beginButton, resultsPageButton, stopButton;

	@FXML
	private LineChart<Integer, Integer> sentimentGraph;

	@FXML
	private TextField topicField;

	@FXML
	// Read test on database
	public void beginAnalysis(ActionEvent event) {
		String str = MainApp.managerM.getText();
		System.out.println(str);
		resultsPageButton.setVisible(true);
		stopButton.setVisible(true);
	}
	
	@FXML
	public void stopAnalysis() {
		System.out.println("need to implement that function");
	}

	@FXML
	public void loadResultsPage(ActionEvent event) {
		try {
			System.out.println("Loading Results Page ...");
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Results.fxml"));
			Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			window.setScene(scene);
			window.show();
			System.out.println("Results page loaded!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}