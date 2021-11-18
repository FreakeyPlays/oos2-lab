package messer;

import java.util.Date;

import org.json.JSONArray;

import senser.AircraftSentence;

public class BasicAircraftFactory {
    public BasicAircraft createAircraft(AircraftSentence as) {
        JSONArray data = as.getData();
        Coordinate coordinate = new Coordinate(data.getDouble(5), data.getDouble(6));
        BasicAircraft ac = new BasicAircraft(data.getString(0), data.getString(1), new Date(data.getLong(4) * 1000),
                coordinate, data.getDouble(9), data.getDouble(10));

        return ac;
    }
}
