package main.resources.application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SQLManager {

	private String ip;
	private int port;
	private String db_name;
	private String db_user;

	public Connection connection;
	private Statement statement;
	private ResultSet result;

	private String getLastDetected = "SELECT * FROM `earthquake` ORDER BY Timestamp DESC ";
	private String insertEarthquake = "INSERT INTO `earthquake`( `Timestamp`, `Position`  ) VALUES (?, ?);";

	private PreparedStatement insertEarthquakeStatement, getLastDetectedStatement;

	public SQLManager(String i, int p, String dbName, String dbUser) {
		try {
			this.ip = i;
			this.port = p;
			this.db_name = dbName;
			this.db_user = dbUser;
			connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + db_name, db_user, "");
			statement = connection.createStatement();

			insertEarthquakeStatement = connection.prepareStatement(insertEarthquake);
			getLastDetectedStatement = connection.prepareStatement(getLastDetected);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addDetectedEarthquake(Timestamp timestamp, double lat, double lon) {
		System.out.println("INSERT START");
		try {
			String position = lat + " " + lon;
			if (lat == 0 && lon == 0) {
				position = "NOT FOUND";
			}
			insertEarthquakeStatement.setTimestamp(1, timestamp);
			insertEarthquakeStatement.setString(2, position);
			int i = insertEarthquakeStatement.executeUpdate();
			System.out.println("Added " + i + " row");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Earthquake> getEarthquakesList(){
		List<Earthquake> list = new ArrayList<Earthquake>();
		Earthquake earthquake = null;
		
		try {
			result = getLastDetectedStatement.executeQuery();
			while (result.next()) {
				Timestamp time = result.getTimestamp("Timestamp");
				String location = result.getString("Position");
				String timeString = time.toString();
				timeString = timeString.substring(0, timeString.length()-2);
				earthquake = new Earthquake(timeString, location);
				list.add(earthquake);
				if(list.size() >= 10)
					break;
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public long getLastDetectedEarthquake() {
		long millis = -1;
		try {
			result = getLastDetectedStatement.executeQuery();
			if (result.first()) {
				Timestamp time = result.getTimestamp("Timestamp");
				millis = time.getTime();
			}
			return millis;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return millis;
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