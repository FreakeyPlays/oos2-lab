package senser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import jsonstream.*;

public class Senser implements Runnable {
	PlaneDataServer server;

	public Senser(PlaneDataServer server) {
		this.server = server;
	}

	public void run() {
		JSONArray planeArray;

		while (true) {
			List<AircraftSentence> planeList = new ArrayList<AircraftSentence>();
			AircraftSentenceFactory acsf = new AircraftSentenceFactory();
			AircraftDisplay display = new AircraftDisplay();

			planeArray = server.getPlaneArray();

			System.out.println("Current Aircrafts in range " + planeArray.length());

			for (int i = 0; i < planeArray.length(); i++) {
				planeList.add(acsf.createSentence(planeArray.getJSONArray(i)));
				display.displayData(planeList.get(i));
			}

		}
	}
}
