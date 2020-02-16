package net.mysmrti.cotprovider.resources;

public class CotMinotaurType {

	String _id;
	Double _lat;
	Double _lon;
	String _name;
	String _type;
	String _category;
	String _threat;
	Double _speed;
	String _date;
	Double _altitude;
	Double _course;
	
	public CotMinotaurType(String id, Double lat, Double lon, String name, String type,
			String category, String threat, Double speed, String date, Double altitude, Double course) {
		_id = id;
		_lat = lat;
		_lon = lon;
		_name = name;
		_type = type;
		_category = category;
		_threat = threat;
		_speed = speed;
		_date = date;
		_altitude = altitude;
		_course = course;
	}
	
	@Override
	public String toString() {
		String result = "{" + "\"type\": \"Feature\"," + 
			"\"id\": \"" + _id + "\"," +
			"\"geometry\": {" + "\"type\": \"Point\"," + "\"coordinates\": [" + _lat + "," + _lon + "]" + "}," + 
			"\"geometry_name\": \"position\"," + 
			"\"properties\": {" +
				"\"name\": \"" + _name + "\"," + 
				"\"type\": \"" + _type + "\"," + 
				"\"fid\": \"" + _id + "\"," + 
				"\"class\": null," + 
				"\"category\": \"" + _category + "\"," + 
				"\"alertLevel\": null," + 
				"\"threat\": \"" + _threat + "\"," + 
				"\"dimension\": \"LND\"," + 
				"\"flag\": null," +
				"\"speed\": " + _speed + "," + 
				"\"dtg\": \"" + _date + "\"," +
				"\"altitude\": " + _altitude + "," + 
				"\"course\": " + _course + "," +
				"\"classification\": \"UNCLASSIFIED\"" + "}" + "}";
		
		return result;
	}
}
