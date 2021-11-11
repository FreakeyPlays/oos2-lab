package senser;

import org.json.JSONArray;

public class AircraftSentence {
    private JSONArray aircraftData;

    public AircraftSentence(JSONArray data) {
        this.aircraftData = data;
    }

    public JSONArray getData() {
        return aircraftData;
    }
}
