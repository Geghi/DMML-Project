package main.resources;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.resources.application.Earthquake;
import main.resources.application.MainApp;

public class ResultsController {
	@FXML
	private TableView<Earthquake> resultsTable;
	
	@FXML
	private TableColumn<Earthquake, String> dateColumn;
	
	@FXML
	private TableColumn<Earthquake, String> locationColumn;

	@FXML
	public void initialize() {
		try {
			dateColumn.setCellValueFactory(new PropertyValueFactory<Earthquake, String>("timestamp"));
			locationColumn.setCellValueFactory(new PropertyValueFactory<Earthquake, String>("location"));
			
			resultsTable.setItems(getEarthquakes());
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public ObservableList<Earthquake> getEarthquakes() {
		ObservableList<Earthquake> earthquakes = FXCollections.observableArrayList();
		earthquakes.addAll(MainApp.manager.getEarthquakesList());
		return earthquakes;
	}
	
	@FXML
	public void loadHomePage(ActionEvent event) {
		try {
			System.out.println("Loading Home Page ...");
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("Home.fxml"));
			Parent root = fxmlLoader.load();
			HomeController homeController = (HomeController) fxmlLoader.getController();
			MainApp.homeController = homeController;
			
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
