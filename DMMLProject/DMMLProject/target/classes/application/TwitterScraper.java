package main.resources.application;

import java.awt.Toolkit;
import java.util.List;

import twitter4j.User;
import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by sveinung on 6/12/15.
 */
public class TwitterScraper {
	private final String consumerKey;
	private final String consumerSecret;
	private final String accessToken;
	private final String accessTokenSecret;
	private OAuth2Token token;
	private Twitter twitter;
	private TwitterStream twitterStream;

	public TwitterScraper(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
	}

	public void search() throws TwitterException, InterruptedException {
		authenticate();
		findTweets("(#terremoto) OR (#magnitudo)");

	}

	public void authenticate() throws TwitterException {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setApplicationOnlyAuthEnabled(true);
		twitter = new TwitterFactory(builder.build()).getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		token = twitter.getOAuth2Token();
		System.out.println("Token " + token + "\n");
	}

	public void authenticateStream() throws TwitterException {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setDebugEnabled(true);
		builder.setOAuthConsumerKey(consumerKey);
		builder.setOAuthConsumerSecret(consumerSecret);
		builder.setOAuthAccessToken(accessToken);
		builder.setOAuthAccessTokenSecret(accessTokenSecret);

		twitterStream = new TwitterStreamFactory(builder.build()).getInstance();

		StatusListener listener = new StatusListener() {
			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStatus(Status status) {
				Toolkit.getDefaultToolkit().beep();
				User user = status.getUser();
				String username = status.getUser().getScreenName();
				System.out.println(username);
				String profileLocation = user.getLocation();
				System.out.println(profileLocation);

//				long tweetId = status.getId();
//				System.out.println(tweetId);
				System.out.println(status.getCreatedAt());
				String content = status.getText();
				System.out.println(content + "\n");
				content = cleanText(content);
				System.out.println(content + "\n");
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());

			}

			@Override
			public void onStallWarning(StallWarning warning) {
				System.out.println("Got stall warning:" + warning);

			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice" + numberOfLimitedStatuses);
			}

		};

		FilterQuery fq = new FilterQuery();
		String[] lang = { "it" };
		fq.language(lang);

//		to track by keywords
		String keywords[] = { "Terremoto", "Magnitudo" };
//		String keywords[] = { "Italia", "youtube" };
		fq.track(keywords);

		// to track by location
//		double[][] locations = {{40,13}, {48,19}};
//	    fq.locations(locations);

		twitterStream.addListener(listener);
		twitterStream.filter(fq);

	}

	public void stopStream() {
		twitterStream.cleanUp();
		twitterStream.shutdown();
		System.out.println("Twitter Stream Terminated.");
	}

	public void findTweets(final String query) throws TwitterException, InterruptedException {
		Query twitterQuery = new Query(query);
		GeoLocation location = new GeoLocation(41.9, 12.5); // latitude, longitude of USA
		twitterQuery.setGeoCode(location, 500, Query.KILOMETERS);
		twitterQuery.setResultType(Query.RECENT);
//		twitterQuery.setUntil("2020-01-17");
		twitterQuery.setLang("it");
		twitterQuery.setCount(100);
		QueryResult queryResult;
		do {
			queryResult = twitter.search(twitterQuery);
			processTweets(queryResult.getTweets());
			Thread.sleep(10000);
		} while ((twitterQuery = queryResult.nextQuery()) != null);
	}

	private void processTweets(List<Status> tweets) {
		try {
			for (Status tweet : tweets) {
				String tw = tweet.getText();
				System.out.println("--------- NEW TWEET ----------");
				System.out.println("--------- RAW TWEET ---------- \n" + tw);			
				tw = cleanText(tw);

				System.out.println("--------- EDITED TWEET --------- \n" + tw + "\n");
				System.out.println(tweet.getGeoLocation());
				System.out.println(tweet.getCreatedAt());

				if (tw != null) {
					if (MainApp.manager.check(tw))
						MainApp.manager.insertTweet(tw);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String cleanText(String text) {
//		remove citations (@name) , links (http://...) and RT at the beginning.
		text = text.replaceAll("(@[^\\s]*)|([R][T][\\s])|(http[^\\s]*)", "");
//		remove non alphabetic / non digit characters. convert to lowercase.
		text = text.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]", " ").toLowerCase().trim();
		text = text.replaceAll(" +", " ").trim();

		return text;
	}

}
