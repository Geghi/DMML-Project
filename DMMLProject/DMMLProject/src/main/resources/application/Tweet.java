package main.resources.application;

public class Tweet {
	private String text;
	private String label;
	private double latitude;
	private double longitude;

	//CONSTRUCTORS
	public Tweet(String text, String label, double latitude, double longitude) {
		this.text = text;
		this.label = label;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Tweet(String text, String label) {
		this.text = text;
		this.label = label;
	}
	
	public Tweet(String text) {
		this.text = text;
	}
	
	
	//GETTERS AND SETTERS
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	
}


