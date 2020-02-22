package main.resources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.resources.application.MainApp;
import main.resources.application.TwitterScraper;
import twitter4j.GeoLocation;
import twitter4j.Status;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stemmers.LovinsStemmer;
import weka.core.stopwords.WordsFromFile;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class HomeController extends Thread {

	@FXML
	public Button beginButton, resultsPageButton, stopButton;

	@FXML
	public Label messageLabel, earthquakeCounterLabel;

	private Thread fetchThread;

	private TwitterScraper twitterScraper;

	public double longitude, latitude;

	public final int warningThreshold = 20, emergencyThreshold = 30, timeSpan = 600000;

	public int earthquakeCounter, geoCounter;

	public FilteredClassifier filteredClassifier;

	public Date currentDate;

	public boolean emergency, stopRealTimeFetch;

	public Instances dataset, trainingSet, unlabeledTweets;

	// GETTERS AND SETTERS
	public boolean getEmergency() {
		return this.emergency;
	}

	public HomeController getHomeController() {
		return this;
	}

	public void setCurrentDate(Date curDate) {
		this.currentDate = curDate;
	}

	public Date getCurrentDate() {
		return this.currentDate;
	}

	public void setStopRealTimeFetch(boolean bool) {
		this.stopRealTimeFetch = bool;
	}

	public boolean getStopRealTimeFetch() {
		return this.stopRealTimeFetch;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getEarthquakeCounter() {
		return this.earthquakeCounter;
	}

	public void setEarthquakeCounter(int count) {
		this.earthquakeCounter = count;
		Platform.runLater(() -> {
			earthquakeCounterLabel.setText(String.valueOf(count));
		});

	}

	public int getGeoCounter() {
		return this.geoCounter;
	}

	public void setGeoCounter(int geoCounter) {
		this.geoCounter = geoCounter;
	}

	public boolean isStopRealTimeFetch() {
		return stopRealTimeFetch;
	}

	public void setEmergency(boolean emergency) {
		this.emergency = emergency;
	}

	public TwitterScraper getTwitterScraper() {
		return this.twitterScraper;
	}

	public int getTimeSpan() {
		return this.timeSpan;
	}

	public void setBeginButtonVisibility(boolean bool) {
		beginButton.setVisible(bool);
	}

	public void setStopButtonVisibility(boolean bool) {
		stopButton.setVisible(bool);
	}

	@FXML
	public void initialize() {
		try {
			twitterScraper = MainApp.twitterScraper;
			trainingSet = getDataFromArffFile("./trainingSet.arff");
			trainingSet.setClassIndex(trainingSet.numAttributes() - 1);

			filteredClassifier = buildClassifier(trainingSet);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@FXML
	public void beginAnalysis(ActionEvent event) {
		try {
			fetchThread = new TweetsFetchingThread();
			fetchThread.start();

			// To test a predefined dataset
//			testStoredDataset("./18gen2017-10min.arff");

			beginButton.setVisible(false);
			stopButton.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public FilteredClassifier buildClassifier(Instances data) {
		FilteredClassifier fc = null;
		try {
			// stemmer
			LovinsStemmer stemmer = new LovinsStemmer();

			// stopwords filter
			WordsFromFile stopwordHandler = new WordsFromFile();
			File stopwordsFile = new File("./src/main/resources/stopwords_it.txt");
			stopwordHandler.setStopwords(stopwordsFile);

			// tokenization + stopword filtering + stemming
			StringToWordVector stringToWordVector = new StringToWordVector(1000);
			stringToWordVector.setOutputWordCounts(true);
			stringToWordVector.setStemmer(stemmer);
			stringToWordVector.setStopwordsHandler(stopwordHandler);

			InfoGainAttributeEval igAttributeEval = new InfoGainAttributeEval();
			Ranker ranker = new Ranker();
			ranker.setOptions(new String[] { "-T", "0.0" });

			// feature selection
			AttributeSelection attSelect = new AttributeSelection();
			attSelect.setEvaluator(igAttributeEval);
			attSelect.setSearch(ranker);

			// feature selection + string to word vector
			MultiFilter multiFilter = new MultiFilter();
			Filter[] twoFilters = new Filter[2];
			twoFilters[0] = stringToWordVector;
			twoFilters[1] = attSelect;
			multiFilter.setFilters(twoFilters);

			// classification algorithm
			SMO smo = new SMO();

			// cost sensitive classification
			CostSensitiveClassifier costSensitiveClassifier = new CostSensitiveClassifier();
			Reader reader = new BufferedReader(new FileReader("./costMatrix.cost"));
			CostMatrix costMatrix = new CostMatrix(reader);
			costSensitiveClassifier.setClassifier(smo);
			costSensitiveClassifier.setCostMatrix(costMatrix);

			fc = new FilteredClassifier();
			fc.setFilter(multiFilter);
			fc.setClassifier(costSensitiveClassifier);
			fc.buildClassifier(data);

			// Evaluation
//			Evaluation eval = new Evaluation(data);
//			eval.crossValidateModel(fc, data, 10, new Random(1));
//			System.out.println(eval.toSummaryString());

			return fc;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fc;
	}

	public void processTweets(List<Status> tweets) {
		try {
			// Create empty dataset
			File output = new File("./unlabeledData.arff");
			PrintWriter pw = new PrintWriter(output);
			dbInit(pw);
			FileWriter fw = new FileWriter(output, true);
			BufferedWriter bw = new BufferedWriter(fw);
			pw = new PrintWriter(bw);

			String out;
			for (Status tweet : tweets) {
				String tw = tweet.getText();
				Date createdAt = tweet.getCreatedAt();
				long difference = currentDate.getTime() - createdAt.getTime();
				// if tweet have been posted less than 10 minutes ago.
				if (difference < timeSpan) {
					// add tweet to dataset.
					tw = cleanText(tw);
					out = "'" + tw + "'," + "?";
					pw.println(out);

				} else {
					twitterScraper.setStop(true);
				}

				// store longitue and latitude for later average computation.
				if (!(tweet.getGeoLocation() == null)) {
					GeoLocation geo = tweet.getGeoLocation();
					latitude += geo.getLatitude();
					longitude += geo.getLongitude();
					geoCounter++;
				}
			}
			pw.close();
			bw.close();
			fw.close();

			dataset = getDataFromArffFile("./unlabeledData.arff");
			earthquakeCounter += countEarthquakes(dataset);
			Platform.runLater(() -> {
				earthquakeCounterLabel.setText(String.valueOf(earthquakeCounter));
			});
			System.out.println("Earthquake Counter: " + earthquakeCounter);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void dbInit(PrintWriter pw) {
		try {
			String out;
			out = "@relation New-Data \n\n@attribute Tweet string\n@attribute Label {earthquake,non-earthquake}\n\n@data";
			pw.println(out);
			pw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public int countEarthquakes(Instances data) {
		int count = 0;
		try {
			data.setClassIndex(data.numAttributes() - 1);
			for (int i = 0; i < data.numInstances(); i++) {
				String predictClass = trainingSet.classAttribute()
						.value((int) filteredClassifier.classifyInstance(data.instance(i)));
				if (predictClass.equals("earthquake")) {
					count++;
					if (count > warningThreshold) {
						if (count > emergencyThreshold) {
							Platform.runLater(() -> {
								messageLabel.setText("An earthquake has been recognized!");
								messageLabel.setStyle("-fx-text-fill: red");
							});
							System.out.println("An Earthquake has been recognized!");
							emergency = true;
							twitterScraper.setStop(true);
							return count;
						} else {
							Platform.runLater(() -> {
								messageLabel.setText("Possible earthquake recognized");
								messageLabel.setStyle("-fx-text-fill: orange");
							});
							System.out.println("Possible earthquake recognized.");
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return count;
	}

	public String cleanText(String text) {
//		remove twitter pic
		text = text.replaceAll("pic.twitter.com/.*", "");
//		replace datetime
		text = text.replaceAll("(\\d{4}-\\d{2}-\\d{2}) | (\\d{4}/\\d{2}/\\d{2})", " ");
		text = text.replaceAll("(\\d{2}:\\d{2}:\\d{2})|UTC", " ");
//		remove citations (@name) , links (http://...) and RT at the beginning.
		text = text.replaceAll("(@[^\\s]*)|([R][T][\\s])|(http[^\\s]*)", "");
//		remove non alphabetic / non digit characters. convert to lowercase.
		text = text.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]", " ").toLowerCase().trim();
		text = text.replaceAll(" +", " ").trim();

		return text;
	}

	public Instances getDataFromArffFile(String path) {
		Instances data = null;
		try {
			DataSource source = new DataSource(path);
			data = source.getDataSet();
			if (data.classIndex() == -1)
				data.setClassIndex(data.numAttributes() - 1);
			return data;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return data;
	}

	public void storeDetectedEarthquake() {
		try {
			// average latitude and longitude.
			if (latitude > 0 && longitude > 0) {
				longitude = longitude / geoCounter;
				latitude = latitude / geoCounter;
			}
			// check if in the last 2 hours an earthquake has already been recognized.
			long lastEarthquake = MainApp.manager.getLastDetectedEarthquake();
			long currentEarthquake = currentDate.getTime();
			if (lastEarthquake != -1) {
				if ((currentEarthquake - lastEarthquake) < (timeSpan * 12)) {
					System.out.println("The earthquake has already been recognized.");
					return;
				}
			}
			// save earthquake to database.
			java.sql.Timestamp startTimestamp = new java.sql.Timestamp(currentDate.getTime());
			System.out.println("Saving earthquake to database...");
			MainApp.manager.addDetectedEarthquake(startTimestamp, latitude, longitude);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void testStoredDataset(String path) {
		try {
			unlabeledTweets = getDataFromArffFile(path);

			// Add class label
			Add add = new Add();
			add.setAttributeName("Label");
			add.setNominalLabels("earthquake,non-earthquake");
			add.setAttributeIndex("last");
			add.setInputFormat(unlabeledTweets);

			unlabeledTweets = Filter.useFilter(unlabeledTweets, add);
			unlabeledTweets.setClassIndex(unlabeledTweets.numAttributes() - 1);

			Instances labeledTweets = new Instances(unlabeledTweets);

			Remove remove = new Remove();
			remove.setAttributeIndices("1,2");
			remove.setInputFormat(unlabeledTweets);

			unlabeledTweets = Filter.useFilter(unlabeledTweets, remove);

			earthquakeCounter += countEarthquakes(unlabeledTweets);

			for (int i = 0; i < unlabeledTweets.numInstances(); i++) {
				String predictClass = trainingSet.classAttribute()
						.value((int) filteredClassifier.classifyInstance(unlabeledTweets.instance(i)));

				labeledTweets.instance(i).setClassValue(predictClass);

				System.out.println("PREDICTED VALUE: " + predictClass + "\t\t INSTANCE N: " + i);
			}

			System.out.println(earthquakeCounter);

			DataSink.write("./labeledTweets.arff", labeledTweets);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@FXML
	public void stopAnalysis(ActionEvent event) {
		try {
			twitterScraper.setStop(true);
			stopRealTimeFetch = true;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@FXML
	public void loadResultsPage(ActionEvent event) {
		try {
			System.out.println("Loading Results Page ...");
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("Results.fxml"));

			Scene scene = new Scene(fxmlLoader.load(), 700, 500);
			Stage stage = new Stage();
			stage.setTitle("TweetQuake");
			stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("Images/AppIcon.png")));
			stage.setScene(scene);
			stage.show();

			System.out.println("Results page loaded!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
