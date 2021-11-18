package messer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.JSONArray;

import observer.Observable;
import observer.Observer;
import senser.AircraftSentence;

public class Messer implements Runnable, Observer<AircraftSentence> {
    Queue<AircraftSentence> queue;

    public Messer() {
        queue = new LinkedList<AircraftSentence>();
    }

    @Override
    public void update(Observable<AircraftSentence> observable, AircraftSentence newValue) {
        queue.add(newValue);
    }

    @Override
    public void run() {
        List<BasicAircraft> aircraftList = new ArrayList<BasicAircraft>();
        BasicAircraftFactory acf = new BasicAircraftFactory();
        BasicAircraftDisplay display = new BasicAircraftDisplay();

        while (true) {
            try {
                BasicAircraft bac = acf.createAircraft(queue.poll());
                aircraftList.add(bac);
                display.displayAircraft(bac);
            } catch (NullPointerException e) {

            }
        }
    }

}
