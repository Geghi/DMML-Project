package main.resources.application;

import org.bson.Document; 

import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.*;

public class MongoManager {
	final private String DB_NAME = "SentimentAnalysis";
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
	
	//Read test on database
	public String getText() {
		String s = null;
		try {
			Document found = (Document) tweetsCollection.find().first();
			if(found != null) {
				s = found.getString("Text");
			}
			return s;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return s;
	}
	public void quit() {
		mongoClient.close();
	}
}