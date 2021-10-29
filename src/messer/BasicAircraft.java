package messer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

public class BasicAircraft {
	private String icao;
	private String operator;
	private Date posTime;
	private Coordinate coordinate;
	private Double speed;
	private Double trak;

	public BasicAircraft(String icao, String operator, Date posTime, Coordinate coordinate, double speed, double trak) {
		this.icao = icao;
		this.operator = operator;
		this.posTime = posTime;
		this.coordinate = coordinate;
		this.speed = speed;
		this.trak = trak;
	}

	public String getIcao() {
		return icao;
	}

	public String getOperator() {
		return operator;
	}

	public Date getPosTime() {
		return posTime;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public double getSpeed() {
		return speed;
	}

	public double getTrak() {
		return trak;
	}

	public static ArrayList<String> getAttributesNames() {
		ArrayList<String> attributes = new ArrayList<String>();

		// TODO: return needed names

		return attributes;
	}

	public static ArrayList<Object> getAttributesValues(BasicAircraft ac) {
		ArrayList<Object> attributes = new ArrayList<Object>();

		// TODO: return needed values

		return attributes;
	}

	@Override
	public String toString() {
		return "BasicAircraft [icao=" + icao + ", operator=" + operator + ", posTime=" + posTime + ", " + coordinate
				+ coordinate + ", speed=" + speed + ", trak =" + trak + "]";
	}
}