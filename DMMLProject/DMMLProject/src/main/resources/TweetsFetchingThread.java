package main.resources;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import main.resources.application.MainApp;

public class TweetsFetchingThread extends Thread {
	
	private Date actualDate;
	
	private HomeController homeController = MainApp.homeController;
	
	private final int timeSpan = homeController.getTimeSpan();
	
	@Override
	public void run() {
		try {
			System.out.println("RUNNING");
			homeController.setStopRealTimeFetch(false);

			while (homeController.getStopRealTimeFetch() == false) {
				
				resetHomeParameters();
				// Start fetching
				homeController.getTwitterScraper().search();
				
				//check how much time to wait before next tweets fetching.
				LocalDateTime date = LocalDateTime.now();
				DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
				String formattedDate = date.format(myFormatObj);
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				actualDate = format.parse(formattedDate);
				long millis =  actualDate.getTime() - homeController.getCurrentDate().getTime();
				
				if(homeController.getStopRealTimeFetch() == false) {
					if(millis < timeSpan) {
						System.out.println("Waiting " + (timeSpan - millis) + "ms before fetching new tweets");
						Thread.sleep(timeSpan - millis);
					}
					System.out.println("Starting new real time fetch...");	
				}
			}
			homeController.setBeginButtonVisibility(true);
			homeController.setStopButtonVisibility(false);
			System.out.println("Real time fetching stopped...");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void resetHomeParameters() {
		homeController.setLatitude(0);
		homeController.setLongitude(0);
		homeController.setGeoCounter(0);
		homeController.setEmergency(false);
		homeController.setEarthquakeCounter(0);
	}
}
