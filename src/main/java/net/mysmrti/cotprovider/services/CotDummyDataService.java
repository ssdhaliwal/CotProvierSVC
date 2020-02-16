package net.mysmrti.cotprovider.services;

import java.net.*;
import java.text.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.apache.commons.lang3.*;
import org.apache.commons.math3.random.*;

import net.mysmrti.cotprovider.application.*;
import net.mysmrti.cotprovider.resources.*;

@Path("/cotdummy")
public class CotDummyDataService extends BaseConfigManager {

	@Context
	ServletContext context;

	@Context
	HttpServletRequest request;

	HashMap<String, Object> _trackList = new HashMap<>(); 
	
	public CotDummyDataService() {
	}

	@Override
	protected void initializeService() throws Exception {
		super.initializeService();
	}

	@GET
	@Path("/status")
	@Produces("text/plain")
	public String getStatus() {
		try {
			this.initializeService();

			String ip = getClientIp();
			System.out.println("client ip/" + ip);
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		return "Cot Dummy Provider";
	}

	@GET
	@Path("/{size}")
	public Response getTracks(@PathParam("size") String size) {

		try {
			this.initializeService();
			String output = generateCotData(Integer.valueOf(size), "-90,90,-180,180");
			return Response.status(200).entity(output).build();
		} catch (Exception ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.build();
		}
	}

	@GET
	@Path("/{size}/{extent}")
	public Response getTracks(@PathParam("size") String size, @PathParam("extent") String extent) {

		try {
			this.initializeService();
			String output = generateCotData(Integer.valueOf(size), extent);
			return Response.status(200).entity(output).build();
		} catch (Exception ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.build();
		}
	}

	@GET
	@Path("/randomize")
	public Response getTracks() {

		try {
			this.initializeService();
			String output = randomizeCotData();
			return Response.status(200).entity(output).build();
		} catch (Exception ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.build();
		}
	}

	private String generateCotData(int length, String extent) {
		Double latmin, latmax, lonmin, lonmax;
		String latlon[] = extent.split(",");

		latmin = Double.valueOf(latlon[0]);
		latmax = Double.valueOf(latlon[1]);
		lonmin = Double.valueOf(latlon[2]);
		lonmax = Double.valueOf(latlon[3]);
		
		String result = "{\"type\": \"FeatureCollection\",\"features\": [";
		RandomGenerator rgKey = new Well1024a((new Date()).getTime());
		RandomDataGenerator random = new RandomDataGenerator(rgKey);

		Date currentTime;
		SimpleDateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:s.SSS'Z'");
		simpleformat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		String id, name, lat, lon, speed, type, category, altitude, course, threat = "UNK";
		int iOffset, nOffset = 0, nMultiplier = 10;
		double iSpeed;

		CotMinotaurType track = null;
		
		for (int i = 0; i < length; i++) {
			id = UUID.randomUUID().toString();
			name = String.format("%06d", random.nextInt(0, 999999));

			lat = String.valueOf(latmin + (latmax - latmin) * rgKey.nextDouble());
			lon = String.valueOf(lonmin + (lonmax - lonmin) * rgKey.nextDouble());

			iOffset = random.nextInt(0, 5);
			if (iOffset == 0) {
				threat = "AIR";
			} else if (iOffset == 1) {
				threat = "FRD";
			} else if (iOffset == 2) {
				threat = "LND";
			} else if (iOffset == 3) {
				threat = "NEU";
			} else if (iOffset == 4) {
				threat = "PND";
			} else if (iOffset == 5) {
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
			track = new CotMinotaurType(id, Double.valueOf(lat), Double.valueOf(lon), name, type, category, 
					threat, Double.valueOf(speed), simpleformat.format(currentTime.getTime()), 
					Double.valueOf(altitude), Double.valueOf(course));
			_trackList.put(id, track);
			
			result += ((i == 0) ? "" : ",") + track.toString();
		}

		try {
			String ip = getClientIp();
			setResource("size_" + ip, length);
			setResource("extent_" + ip, extent);
			setResource("tracks_" + ip, _trackList);
		} catch (Exception ex) {
			System.out.println(ex);
		}

		currentTime = new Date();
		result += "],\"totalFeatures\": \"unknown\",\"numberReturned\": " + _trackList.size() + ",\"timeStamp\": \"" + 
				simpleformat.format(currentTime.getTime()) + "\","
				+ "\"crs\": {\"type\": \"name\",\"properties\": {\"name\": \"urn:ogc:def:crs:EPSG::4326\"}}}";
		return result;
	}

	private String getClientIp() throws Exception {
		String ip = request.getRemoteAddr();
		if (ip.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
		    InetAddress inetAddress = InetAddress.getLocalHost();
		    String ipAddress = inetAddress.getHostAddress();
		    ip = ipAddress;
		}
		
		return ip;
	}
	
	private String randomizeCotData() {
		String result = "";

		try {
			String ip = getClientIp();

			int size = Integer.valueOf(getResource("size_" + ip, "0").toString());
			String extent = getResource("extent_" + ip, "-90,90,-180,180").toString();
			HashMap<String, Object> tracks = (HashMap<String, Object>)getResource("tracks_" + ip, null);
			
			int pctAdd = (int)Math.round(size * .01); 
			int pctUpdate = (int)Math.round(size * .05); 
			int pctRemove = (int)Math.round(size * .02);
			
			System.out.println(pctAdd + ", " + pctUpdate + ", " + pctRemove);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		
		return result;
	}

}