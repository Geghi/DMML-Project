package main.resources.application;

import javafx.beans.property.SimpleStringProperty;

public class Earthquake {
	private SimpleStringProperty timestamp, location;

	public Earthquake(String timestamp, String location) {
		this.timestamp = new SimpleStringProperty(timestamp);
		this.location = new SimpleStringProperty(location);

	}

	public String getTimestamp() {
		return timestamp.get();
	}

	public void setTimestamp(SimpleStringProperty timestamp) {
		this.timestamp = timestamp;
	}

	public String getLocation() {
		return location.get();
	}

	public void setLocation(SimpleStringProperty location) {
		this.location = location;
	}

}
