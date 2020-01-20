package main.resources.application;

import java.sql.*;
import java.util.*;

public class SQLManager {

	private String ip;
	private int port;
	private String db_name;
	private String db_user;

	public Connection connection;
	private Statement statement;
	private ResultSet result;

	private String selectionTweetText = "SELECT T.Text FROM Tweets T WHERE T.id = 1;";
	private PreparedStatement selectionTweetStatement;

	public SQLManager(String i, int p, String dbName, String dbUser) {
		try {
			this.ip = i;
			this.port = p;
			this.db_name = dbName;
			this.db_user = dbUser;
			connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + db_name, db_user, "");
			statement = connection.createStatement();

			selectionTweetStatement = connection.prepareStatement(selectionTweetText);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getText() {
		String text = null;
		try {
			result = selectionTweetStatement.executeQuery();
        	 if(!result.first()) {
                 return null;
             }
            text = result.getString("Text");
        } 
        catch (SQLException e) {e.printStackTrace();}
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