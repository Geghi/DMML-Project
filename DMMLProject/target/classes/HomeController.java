package main.resources;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class HomeController {

	@FXML
	private Button beginButton;
	
	@FXML
	private LineChart<Integer, Integer> sentimentGraph;
	
	@FXML
	private TextField topicField;
	
}
