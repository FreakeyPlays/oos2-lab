package senser;

import org.json.JSONArray;

public class AircraftSentenceFactory {
    public AircraftSentence createSentence(JSONArray data) {
        AircraftSentence acs = new AircraftSentence(data);
        return acs;
    }
}
