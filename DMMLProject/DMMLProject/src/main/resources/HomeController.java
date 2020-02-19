package main.resources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Random;
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
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stemmers.LovinsStemmer;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stopwords.WordsFromFile;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;

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
//		MainApp.manager.cleanDbText();
	}



	@FXML
	public void beginAnalysis(ActionEvent event) {
		try {

//			MainApp.twitterScraper.authenticateStream(); // start getting real-time tweets from Twitter Stream
//			MainApp.twitterScraper.search();



////			//createArffFile(words, label);
//			
			Instances data;
		
			data = getDataFromArffFile();
//			data = getDataFromSQLDatabase();
			buildClassifier(data);
			
//			MainApp.manager.saveArffToDb(data);
			
			// TODO list
			// CLASSIFICATION

			// UPDATE GRAPH

			// WARNING/EMERGENCY MESSAGE

			// update buttons
			resultsPageButton.setVisible(true);
			stopButton.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
	//text to lowercase
	//String lettersLowerCase = text.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\\" \"]", " ").trim().toLowerCase();
	//lettersLowerCase = lettersLowerCase.replaceAll(" +", " ");


	public void createArffFile(ArrayList<ArrayList<String>> stems, String[] label) {
		try {
			File file = new File("./tweets.arff");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			PrintWriter out = new PrintWriter(bw);
			String line = "";
			out.println("@RELATION tweets\n");
			out.println("@ATTRIBUTE Text STRING");
			out.println("@ATTRIBUTE Label {earthquake,non-earthquake}\n");
			out.println("@DATA\n");
			for (int i = 0; i < stems.size(); i++) {
				line = "'";
				for (int j = 0; j < stems.get(i).size(); j++) {
					line += stems.get(i).get(j);
					if (j < stems.get(i).size())
						line += " ";
				}
				if (line.equals("'"))
					continue;
				System.out.println(label[i]);
				line += "'," + label[i];
				out.println(line);
			}
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@FXML
	public void stopAnalysis(ActionEvent event) {
		try {
			MainApp.twitterScraper.stopStream();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Instances getDataFromArffFile() {
		Instances data = null;
		try {
			DataSource source = new DataSource("./tweets.arff");
			data = source.getDataSet();
			if (data.classIndex() == -1)
				data.setClassIndex(data.numAttributes() - 1);
			return data;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return data;
	}
	
	public Instances getDataFromSQLDatabase() {
		Instances data = null;
		try {
			InstanceQuery query = new InstanceQuery();
			query.setUsername("root");
			query.setPassword("");
			query.setDatabaseURL("jdbc:mysql://localhost:3306/tweets");
			query.setQuery("select Text , Label from tweet");
	
			data = query.retrieveInstances();
			if (data.classIndex() == -1)
				data.setClassIndex(data.numAttributes() - 1);
			
			return data;
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return data;
	}

	public FilteredClassifier buildClassifier(Instances data) {
		FilteredClassifier fc = null;
		try {		
			//stemmer
			LovinsStemmer stemmer = new LovinsStemmer();
		
			//stopwords filter
			WordsFromFile stopwordHandler = new WordsFromFile();
			File stopwordsFile = new File("./src/main/resources/stopwords_it.txt");
			stopwordHandler.setStopwords(stopwordsFile);
			
			//tokenization + stopword filtering + stemming
			StringToWordVector filter = new StringToWordVector(1000);
			filter.setOutputWordCounts(true);
			filter.setStemmer(stemmer);
			filter.setStopwordsHandler(stopwordHandler);
			filter.setInputFormat(data);
			data = Filter.useFilter(data, filter);
			
			InfoGainAttributeEval igAttributeEval = new InfoGainAttributeEval();
			Ranker ranker = new Ranker();
			ranker.setOptions(new String[] {"-T", "0.0"});
			
			//feature selection
			AttributeSelection attSelect = new AttributeSelection();
			attSelect.setEvaluator(igAttributeEval);
			attSelect.setSearch(ranker);
			attSelect.setInputFormat(data);
			
			//classification algorithm
			SMO smo = new SMO();
			
			fc = new FilteredClassifier();
			fc.setFilter(attSelect);
			fc.setClassifier(smo);
			fc.buildClassifier(data);
			

			// PREDICT TEST ON THE SAME DATA
			for (int i = 0; i < data.numInstances(); i++) {
				String actualValue = data.classAttribute().value((int) data.instance(i).classValue());
				String predict = data.classAttribute()
						.value((int) fc.classifyInstance(data.instance(i)));
				if (!predict.equals(actualValue))
					System.out.println("ACTUAL VALUE: " + actualValue + "\t\t PREDICTED VALUE: " + predict
							+ "\t\t INSTANCE N: " + i);
			}

			//Evaluation
			Evaluation eval = new Evaluation(data);
			eval.crossValidateModel(fc, data, 5, new Random(1));
			System.out.println(eval.toSummaryString());
			return fc;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fc;
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
