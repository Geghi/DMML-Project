package main.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

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

	Set<String> stopWords = new LinkedHashSet<String>();

	@FXML
	public void initialize() {
		getStopWords();
	}

	public void getStopWords() {
		try {
			File file = new File("./src/main/resources/stopwords_en.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			for (String line; (line = br.readLine()) != null;)
				stopWords.add(line.trim());
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@FXML
	// Read test on database
	public void beginAnalysis(ActionEvent event) {
		try {
			// TODO scrape tweets in real time.

			// test text, later we will use scraped tweets text
			String text = MainApp.manager.getText();
			
			ArrayList<String> words = textToWords(text);
			
			//Stemming algorithm.

			//Create Bag of words.
			
			// update buttons
			resultsPageButton.setVisible(true);
			stopButton.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//Given a text of a tweet it returns the list of meaningful words.
	public ArrayList<String> textToWords(String text) {
		String lettersLowerCase = text.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\\" \"]", "").toLowerCase();
		System.out.println(lettersLowerCase);
		// list of words
		ArrayList<String> words = new ArrayList<>(Arrays.asList(lettersLowerCase.split(" ")));
		
		ArrayList<String> filteredWords = new ArrayList<>();
		int i = 0;
		while (i < words.size()) {
			if (!stopWords.contains(words.get(i))) {
				filteredWords.add((String) words.get(i));
			}
			i++;
		}
		return filteredWords;
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
