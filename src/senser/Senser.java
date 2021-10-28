package senser;

import org.json.JSONArray;

import jsonstream.*;

public class Senser implements Runnable
{
	PlaneDataServer server;

	public Senser(PlaneDataServer server)
	{
		this.server = server;
	}

	private String getSentence()
	{
		String list = server.getPlaneListAsString();
		return list;
	}
	
	public void run() {
		String[] aircraftList;
		JSONArray planeArray;

		while (true) {
			// aircraftList = getSentence().split("],");
			// for (String i : aircraftList) {
			// 	System.out.print(i);
			// 	if (!aircraftList[aircraftList.length - 1].equals(i))
			// 		System.out.println("],");
			// }
			// System.out.println();

			planeArray = server.getPlaneArray();
			for (int i = 0; i < planeArray.length(); i++) {
				System.out.println(planeArray.getJSONArray(i));
			}
		}
	}
}