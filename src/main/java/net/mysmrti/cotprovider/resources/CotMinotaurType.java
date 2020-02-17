package net.mysmrti.cotprovider.resources;

public class CotMinotaurType {

	private String _id;
	private Double _lat;
	private Double _lon;
	private String _name;
	private String _type;
	private String _category;
	private String _threat;
	private Double _speed;
	private String _date;
	private Double _altitude;
	private Double _course;

	public CotMinotaurType(String id, Double lat, Double lon, String name, String type, String category, String threat,
			Double speed, String date, Double altitude, Double course) {
		set_id(id);
		set_lat(lat);
		set_lon(lon);
		set_name(name);
		set_type(type);
		set_category(category);
		set_threat(threat);
		set_speed(speed);
		set_date(date);
		set_altitude(altitude);
		set_course(course);
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public Double get_lat() {
		return _lat;
	}

	public void set_lat(Double _lat) {
		this._lat = _lat;
	}

	public Double get_lon() {
		return _lon;
	}

	public void set_lon(Double _lon) {
		this._lon = _lon;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public String get_type() {
		return _type;
	}

	public void set_type(String _type) {
		this._type = _type;
	}

	public String get_category() {
		return _category;
	}

	public void set_category(String _category) {
		this._category = _category;
	}

	public String get_threat() {
		return _threat;
	}

	public void set_threat(String _threat) {
		this._threat = _threat;
	}

	public Double get_speed() {
		return _speed;
	}

	public void set_speed(Double _speed) {
		this._speed = _speed;
	}

	public String get_date() {
		return _date;
	}

	public void set_date(String _date) {
		this._date = _date;
	}

	public Double get_altitude() {
		return _altitude;
	}

	public void set_altitude(Double _altitude) {
		this._altitude = _altitude;
	}

	public Double get_course() {
		return _course;
	}

	public void set_course(Double _course) {
		this._course = _course;
	}

	public void movePoint() {
		double latitude = this.get_lat();
		double longitude = this.get_lon();
		double distanceInMetres = this.get_speed();
		double bearing = this.get_course();
		
		double brngRad = Math.toRadians(bearing);
		double latRad = Math.toRadians(latitude);
		double lonRad = Math.toRadians(longitude);
		int earthRadiusInMetres = 6371000;
		double distFrac = distanceInMetres / earthRadiusInMetres;

		double latitudeResult = Math.asin(
				Math.sin(latRad) * Math.cos(distFrac) + Math.cos(latRad) * Math.sin(distFrac) * Math.cos(brngRad));
		double a = Math.atan2(Math.sin(brngRad) * Math.sin(distFrac) * Math.cos(latRad),
				Math.cos(distFrac) - Math.sin(latRad) * Math.sin(latitudeResult));
		double longitudeResult = (lonRad + a + 3 * Math.PI) % (2 * Math.PI) - Math.PI;

		this.set_lat(Math.toDegrees(latitudeResult));
		this.set_lon(Math.toDegrees(longitudeResult));
	}

	@Override
	public String toString() {
		String result = "{" + "\"type\": \"Feature\"," + "\"id\": \"" + _id + "\"," + "\"geometry\": {"
				+ "\"type\": \"Point\"," + "\"coordinates\": [" + _lat + "," + _lon + "]" + "},"
				+ "\"geometry_name\": \"position\"," + "\"properties\": {" + "\"name\": \"" + _name + "\","
				+ "\"type\": \"" + _type + "\"," + "\"fid\": \"" + _id + "\"," + "\"class\": null," + "\"category\": \""
				+ _category + "\"," + "\"alertLevel\": null," + "\"threat\": \"" + _threat + "\","
				+ "\"dimension\": \"LND\"," + "\"flag\": null," + "\"speed\": " + _speed + "," + "\"dtg\": \"" + _date
				+ "\"," + "\"altitude\": " + _altitude + "," + "\"course\": " + _course + ","
				+ "\"classification\": \"UNCLASSIFIED\"" + "}" + "}";

		return result;
	}
}
