package main.resources.application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import main.resources.HomeController;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterScraper {
	private final String consumerKey, consumerSecret;
	private OAuth2Token token;
	private Twitter twitter;
	public boolean stop = false;
	public HomeController homeController;
	private final int fetchNextPageWaitingTime = 10000;

	public TwitterScraper(String consumerKey, String consumerSecret) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
	}

	public void setStop(boolean bool) {
		this.stop = bool;
	}

	public boolean getStop() {
		return this.stop;
	}

	public void search() throws TwitterException, InterruptedException, ParseException {
		authenticate();
		findTweets("terremoto");
	}

	public void authenticate() throws TwitterException {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setApplicationOnlyAuthEnabled(true);
		twitter = new TwitterFactory(builder.build()).getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		token = twitter.getOAuth2Token();
		System.out.println("Token " + token + "\n");
	}

	public void findTweets(final String query) throws TwitterException, InterruptedException, ParseException {
		homeController = MainApp.getHomeController();
		setCurrentDate();
		homeController.resetEarthquakeCounter();
		Query twitterQuery = new Query(query);
//		GeoLocation location = new GeoLocation(41.9, 12.5); // latitude, longitude
//		twitterQuery.setGeoCode(location, 100, Query.KILOMETERS);
		twitterQuery.setResultType(Query.RECENT);
		twitterQuery.setLang("it");
		twitterQuery.setCount(100);
		QueryResult queryResult;

		//start processing each page (until there are no more pages).
		do {
			queryResult = twitter.search(twitterQuery);
			homeController.processTweets(queryResult.getTweets());
			//wait before fetching next tweets page.
			Thread.sleep(fetchNextPageWaitingTime);
		} while ((twitterQuery = queryResult.nextQuery()) != null && stop == false);
		stop = false;
		if(homeController.getEmergency() == true) {
			homeController.storeDetectedEarthquake();
			homeController.setEmergency(false);
		}
	}

	public void setCurrentDate() {
		try {
			LocalDateTime date = LocalDateTime.now();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
			String formattedDate = date.format(myFormatObj);
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			homeController.setCurrentDate(format.parse(formattedDate));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
