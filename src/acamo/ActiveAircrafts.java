package acamo;

import java.util.ArrayList;
import java.util.HashMap;

import messer.BasicAircraft;
import observer.Observable;
import observer.Observer;

public class ActiveAircrafts implements Observer<BasicAircraft>, ActiveAircraftsInterface {

    private HashMap<String, BasicAircraft> activeAircrafts;

    public ActiveAircrafts() {
        activeAircrafts = new HashMap<String, BasicAircraft>();
    }

    public synchronized void store(String icao, BasicAircraft bac) {
        activeAircrafts.put(icao, bac);
    }

    public synchronized void clear() {
        activeAircrafts.clear();
    }

    public synchronized BasicAircraft retrieve(String icao) { // Maby check for null and return jsut null
        BasicAircraft bac = activeAircrafts.get(icao);
        return bac;
    }

    public synchronized ArrayList<BasicAircraft> values() {
        ArrayList<BasicAircraft> bacList = new ArrayList<BasicAircraft>(activeAircrafts.values());
        return bacList;
    }

    public String toString() {
        return activeAircrafts.toString();
    }

    @Override
    public void update(Observable<BasicAircraft> observable, BasicAircraft newValue) {
        this.store(newValue.getIcao(), newValue);
    }

}
