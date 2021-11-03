package senser;

public class AircraftSentenceFactory {
    public AircraftSentence createSentence(String data) {
        AircraftSentence acs = new AircraftSentence(data);
        return acs;
    }
}
