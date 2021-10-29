package senser;

public class AircraftSentenceFactory {
    public void addSentence(String s) {
        AircraftSentence acs = new AircraftSentence(s);
        acs.display(acs);
    }
}
