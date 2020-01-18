package main.resources.application;

import org.bson.Document; 

import com.mongodb.client.*;

public class MongoManager {
	final private String DB_NAME = "TweetSentiment";
	private int port;
	private String ip;
	MongoClient mongoClient;
	MongoDatabase database;
	MongoCollection<Document> tweetsCollection;
	
	public MongoManager(int port, String ip) {
		this.port = port;
		this.ip = ip;
		mongoClient = MongoClients.create("mongodb://" + this.ip + ":" + this.port);
		database = mongoClient.getDatabase(DB_NAME);
		tweetsCollection = database.getCollection("tweetsCollection");
	}
	
	public void quit() {
		mongoClient.close();
	}
}
