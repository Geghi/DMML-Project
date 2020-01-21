package main.resources.application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLManager {

	private String ip;
	private int port;
	private String db_name;
	private String db_user;

	public Connection connection;
	private Statement statement;
	private ResultSet result;

	private String selectionTweetText = "SELECT T.Text FROM tweets T WHERE T.id = 1;";
	private String insertTweetText = "INSERT INTO `tweets`( `Text` ) VALUES (?);";
	private String selectionTweetCheckText = "SELECT * FROM tweets T WHERE T.Text = ?;";

	private PreparedStatement selectionTweetStatement, selectionTweetCheckStatement, insertTweetStatement;

	public SQLManager(String i, int p, String dbName, String dbUser) {
		try {
			this.ip = i;
			this.port = p;
			this.db_name = dbName;
			this.db_user = dbUser;
			connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + db_name, db_user, "");
			statement = connection.createStatement();

			selectionTweetStatement = connection.prepareStatement(selectionTweetText);
			insertTweetStatement = connection.prepareStatement(insertTweetText);
			selectionTweetCheckStatement = connection.prepareStatement(selectionTweetCheckText);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertTweet(String tweet) {
		System.out.println("INSERT START");
		try {
			insertTweetStatement.setString(1, tweet);
			int i = insertTweetStatement.executeUpdate();
			System.out.println("Added " + i + " row");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//return true if tweet is not present in the database, false otherwise.
	public Boolean check(String tweet) {
		try {
			selectionTweetCheckStatement.setString(1, tweet);
			result = selectionTweetCheckStatement.executeQuery();
			if (!result.first()) {
				return true;
			}
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public String getText() {
		String text = null;
		try {
			result = selectionTweetStatement.executeQuery();
			if (!result.first()) {
				return null;
			}
			text = result.getString("Text");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return text;
	}

	// stop connection
	void quit() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}