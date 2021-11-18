package senser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import jsonstream.*;
import observer.SimpleObservable;

public class Senser extends SimpleObservable<AircraftSentence> implements Runnable {
	private boolean lab1 = false;

	PlaneDataServer server;
	AircraftSentence sentence;

	public Senser(PlaneDataServer server) {
		this.server = server;
	}

	public void run() {
		JSONArray planeArray;
		List<AircraftSentence> planeList = new ArrayList<AircraftSentence>();
		AircraftSentenceFactory acsf = new AircraftSentenceFactory();
		AircraftSentenceDisplay display = new AircraftSentenceDisplay();

		while (true) {

			planeArray = server.getPlaneArray();

			planeList = acsf.createSentenceList(planeArray);

			if (lab1) {
				System.out.println("Current Aircrafts in range " + planeArray.length());

				for (AircraftSentence acs : planeList) {
					display.displayData(acs);
				}
			}

			if (!lab1) {
				for (AircraftSentence acs : planeList) {
					setChanged();
					notifyObservers(acs);
				}
			}

		}
	}

}
