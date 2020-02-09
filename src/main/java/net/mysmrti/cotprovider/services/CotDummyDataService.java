package net.mysmrti.cotprovider.services;

import java.text.*;
import java.util.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.*;
import org.apache.commons.math3.random.*;

@Path("/cotdummy")
public class CotDummyDataService {

	@GET
	@Path("/{param}")
	public Response getMsg(@PathParam("param") String msg) {

		String output = generateCotData(Integer.valueOf(msg));

		return Response.status(200).entity(output).build();

	}

	private String generateCotData(int length) {
		String result = "{\"type\": \"FeatureCollection\",\"features\": [";
		RandomGenerator rgKey = new Well1024a((new Date()).getTime());
		RandomDataGenerator random = new RandomDataGenerator(rgKey);

		Date currentTime;
		SimpleDateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:s.SSS'Z'");
		simpleformat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		String id, name, lat, lon, speed, type, category, altitude, course, threat = "UNK";
		int iOffset, nOffset = 0, nMultiplier = 10;
		double iSpeed;

		for (int i = 0; i < length; i++) {
			id = UUID.randomUUID().toString();
			name = String.format("%06d", random.nextInt(0, 999999));

			lat = String.valueOf(random.nextInt(-90, 90) + rgKey.nextDouble());
			lon = String.valueOf(random.nextInt(-180, 180) + rgKey.nextDouble());

			iOffset = random.nextInt(0, 2);
			if (iOffset == 0) {
				threat = "NEU";
			} else if (iOffset == 1) {
				threat = "PND";
			} else if (iOffset == 2) {
				threat = "UNK";
			}

			iOffset = random.nextInt(0, 3);
			if (iOffset == 0) {
				nOffset = 50;
			} else if (iOffset == 1) {
				nOffset = 500;
			} else if (iOffset == 2) {
				nOffset = 3000;
			} else if (iOffset == 3) {
				nOffset = 5000;
			}
			speed = String.valueOf(random.nextInt(0, nOffset) + rgKey.nextDouble()).substring(0, 6);

			type = "";
			category = "";
			iSpeed = Double.valueOf(speed);
			if (iSpeed <= 50) {
				type = "SEA SURFACE TRACK";
				category = "SEA";
			} else if ((iSpeed > 50) && (iSpeed <= 150)) {
				type = "GROUND TRACK";
				category = "LND";
			} else if ((iSpeed > 150) && (iSpeed <= 1200)) {
				type = "AIR TRACK";
				category = "AIR";
			} else if ((iSpeed > 1200) && (iSpeed <= 3000)) {
				type = "MISSLE TRACK";
				category = "MSL";
			} else if (iSpeed > 3000) {
				type = "UFO";
				category = "UFO";
			}

			if (iOffset == 0) {
				nMultiplier = 1;
			} else if (iOffset == 1) {
				nMultiplier = 10;
			} else if (iOffset == 2) {
				nMultiplier = 100;
			} else if (iOffset == 3) {
				nMultiplier = 1000;
			}
			altitude = String.valueOf(rgKey.nextDouble() * nMultiplier).substring(0, 6);

			if (category == "MSL") {
				course = "0.00";
			} else {
				course = String.valueOf(random.nextInt(0, 360) + rgKey.nextDouble()).substring(0, 6);
			}

			currentTime = new Date();
			result += ((i == 0) ? "" : ",") + "{" + "\"type\": \"Feature\"," + "\"id\": \"" + id + "\","
					+ "\"geometry\": {" + "    \"type\": \"Point\"," + "    \"coordinates\": [" + "        " + lat + ","
					+ "        " + lon + "" + "    ]" + "}," + "\"geometry_name\": \"position\"," + "\"properties\": {"
					+ "    \"name\": \"" + name + "\"," + "    \"type\": \"" + type + "\"," + "    \"fid\": \"" + id
					+ "\"," + "    \"class\": null," + "    \"category\": \"" + category + "\"," + "    \"alertLevel\": null,"
					+ "    \"threat\": \"" + threat + "\"," + "    \"dimension\": \"LND\"," + "    \"flag\": null,"
					+ "    \"speed\": " + speed + "," + "    \"dtg\": \"" + simpleformat.format(currentTime.getTime()) + "\","
					+ "    \"altitude\": " + altitude + "," + "    \"course\": " + course + ","
					+ "    \"classification\": \"UNCLASSIFIED\"" + "}" + "}";
		}

		currentTime = new Date();
		result += "],\"totalFeatures\": \"unknown\",\"numberReturned\": " + length + ",\"timeStamp\": \"" + 
				simpleformat.format(currentTime.getTime()) + "\","
				+ "\"crs\": {\"type\": \"name\",\"properties\": {\"name\": \"urn:ogc:def:crs:EPSG::4326\"}}}";
		return result;
	}

}