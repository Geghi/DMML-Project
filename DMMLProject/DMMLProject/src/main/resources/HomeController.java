package main.resources;

import main.resources.application.MainApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

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
			File file = new File("./src/main/resources/stopwords_it.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			for (String line; (line = br.readLine()) != null;)
				stopWords.add(line.trim());
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@FXML
	public void beginAnalysis(ActionEvent event) {
		try {
			//start getting real-time tweets from Twitter Stream
			MainApp.twitterScraper.authenticateStream();
			
			// test text, later we will use scraped tweets text
			String text = MainApp.manager.getText();

//			String words = textToWords(text);
			ArrayList<String> words = textToWords(text);

			//Stemming
			words = stemmingProcess(words);
			System.out.println("\n" + words);
			
			//TODO list
			// Create Bag of words.

			// feature representation
			
			//feature selection
			
			//apply classifier
			
			//update graph and send message in case of earthquake detection.
			
			
			
			
			//update buttons
			resultsPageButton.setVisible(true);
			stopButton.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// Given a text of a tweet it returns the list of meaningful words.
	public ArrayList<String> textToWords(String text) {
		String lettersLowerCase = text.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\\" \"]", " ").trim().toLowerCase();
		lettersLowerCase = lettersLowerCase.replaceAll(" +", " ");
		System.out.println("TWEET WORDS: " + lettersLowerCase + "\n");
		// list of words
		ArrayList<String> words = new ArrayList<>(Arrays.asList(lettersLowerCase.split(" ")));
//		String filteredWords = "";
		ArrayList<String> filteredWords = new ArrayList<>();
		int i = 0;
		while (i < words.size()) {
			if (!stopWords.contains(words.get(i))) {
				filteredWords.add(words.get(i));
//				filteredWords += words.get(i) + " ";
			}
			i++;
		}
		System.out.println("TWEET WORDS AFTER FILTERING: " + filteredWords + "\n");
		return filteredWords;
	}

	public ArrayList<String> stemmingProcess(ArrayList<String> words) {
		SnowballStemmer stemmer = new englishStemmer();
		for (int i = 0; i < words.size(); i++) {
			stemmer.setCurrent(words.get(i));
			stemmer.stem();
			String stem = stemmer.getCurrent();
			System.out.println("WORD: " + words.get(i) + "\t\t\t\tSTEM: " + stem);
			words.set(i, stem);
		}
		return words;
	}

	@FXML
	public void stopAnalysis(ActionEvent event) {
		try {
			MainApp.twitterScraper.stopStream();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
