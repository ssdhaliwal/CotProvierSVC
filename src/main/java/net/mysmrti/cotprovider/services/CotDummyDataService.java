package net.mysmrti.cotprovider.services;

import java.net.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;

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
			this.showBaseUrl();
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
			long startTime = System.nanoTime();
			
			this.initializeService();
			String output = generateCotData(Integer.valueOf(size), "-90,90,-180,180");
			
			long endTime = System.nanoTime();
			showExecutionTime("getTracks/{" + size + "}", startTime, endTime);
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
			long startTime = System.nanoTime();
			
			this.initializeService();
			String output = generateCotData(Integer.valueOf(size), extent);
			
			long endTime = System.nanoTime();
			showExecutionTime("getTracks/{" + size + "}/{" + extent + "}", startTime, endTime);
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
			long startTime = System.nanoTime();
			
			this.initializeService();
			String output = randomizeCotData(0, 0);
			
			long endTime = System.nanoTime();
			showExecutionTime("getTracks/randomize", startTime, endTime);
			return Response.status(200).entity(output).build();
		} catch (Exception ex) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.build();
		}
	}

	@GET
	@Path("/randomize/{add}/{update}")
	public Response getTracks(@PathParam("add") int add, @PathParam("update") int update) {

		try {
			long startTime = System.nanoTime();
			
			this.initializeService();
			String output = randomizeCotData(add, update);
			
			long endTime = System.nanoTime();
			showExecutionTime("getTracks/randomize/{" + add + "}/{" + update + "}", startTime, endTime);
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
		
		StringBuilder result = new StringBuilder();
		result.append("{\"type\": \"FeatureCollection\",\"features\": [");
		
		RandomGenerator rgKey = new Well1024a((new Date()).getTime());
		RandomDataGenerator random = new RandomDataGenerator(rgKey);

		Date currentTime;
		SimpleDateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:s.SSS'Z'");
		simpleformat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		HashMap<String, Object> tracks = new HashMap<String, Object>();
		CotMinotaurType track = null;
		
		for (int i = 0; i < length; i++) {
			track = getRandomTrack(random, rgKey, simpleformat, latmin, latmax, lonmin, lonmax);
			tracks.put(track.get_id(), track);
			
			result.append(((i == 0) ? "" : ",") + track.toString());
		}

		try {
			String ip = getClientIp();
			setResource("size_" + ip, length);
			setResource("extent_" + ip, extent);
			setResource("tracks_" + ip, tracks);
		} catch (Exception ex) {
			System.out.println(ex);
		}

		currentTime = new Date();
		result.append("],\"totalFeatures\": \"unknown\",\"numberReturned\": " + tracks.size() + ",\"timeStamp\": \"" + 
				simpleformat.format(currentTime.getTime()) + "\","
				+ "\"crs\": {\"type\": \"name\",\"properties\": {\"name\": \"urn:ogc:def:crs:EPSG::4326\"}}}");
		return result.toString();
	}
	
	private CotMinotaurType getRandomTrack(RandomDataGenerator random, RandomGenerator rgKey, SimpleDateFormat simpleformat,
			Double latmin, Double latmax, Double lonmin, Double lonmax) {
		CotMinotaurType track = null;

		Date currentTime;

		String id, name, lat, lon, speed, type, category, altitude, course, threat = "UNK";
		int iOffset, nOffset = 0, nMultiplier = 10;
		double iSpeed;
		
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

		return track;
	}

	private String randomizeCotData(int add, int update) {
		StringBuilder result = new StringBuilder();
		result.append("{\"type\": \"FeatureCollection\",\"features\": [");

		Double latmin, latmax, lonmin, lonmax;

		RandomGenerator rgKey = new Well1024a((new Date()).getTime());
		RandomDataGenerator random = new RandomDataGenerator(rgKey);

		Date currentTime;
		SimpleDateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:s.SSS'Z'");
		simpleformat.setTimeZone(TimeZone.getTimeZone("GMT"));

		try {
			String ip = getClientIp();

			int size = Integer.valueOf(getResource("size_" + ip, "0").toString());
			String extent = getResource("extent_" + ip, "-90,90,-180,180").toString();
			String latlon[] = extent.split(",");

			latmin = Double.valueOf(latlon[0]);
			latmax = Double.valueOf(latlon[1]);
			lonmin = Double.valueOf(latlon[2]);
			lonmax = Double.valueOf(latlon[3]);

			HashMap<String, Object> tracks = (HashMap<String, Object>)getResource("tracks_" + ip, null);
			HashMap<String, Object> trackUpdates = new HashMap<String, Object>();
			
			int pctAdd = add;
			int pctUpdate = update;
			if ((add == 0) && (update == 0)) {
				pctAdd = (int)Math.round(size * .01); 
				pctUpdate = (int)Math.round(size * .05);
			}

			CotMinotaurType track = null;
			String[] trackKeys = (String[])tracks.keySet().toArray(new String[0]);
			ArrayList<String> removeList = new ArrayList<>();

			// add new and remove old tracks
			ArrayList<Integer> rows = new ArrayList<>();
			for (int i = 0; i < pctAdd; i++) {
				rows.add(random.nextInt(0, size-1));
				track = (CotMinotaurType)tracks.get(trackKeys[rows.get(rows.size()-1)]);
				
				removeList.add(track.get_id());
			}
			for(String key : removeList) {
				tracks.remove(key);

				track = getRandomTrack(random, rgKey, simpleformat, latmin, latmax, lonmin, lonmax);
				tracks.put(track.get_id(), track);
				trackUpdates.put(track.get_id(), track);

				System.out.println("track-remove, " + key);
				System.out.println("track-add   , " + track.get_id());
			}

			// update tracks
			trackKeys = (String[])tracks.keySet().toArray(new String[0]);
			rows.clear();
			for (int i = 0; i < pctUpdate; i++) {
				rows.add(random.nextInt(0, size-1));

				track = (CotMinotaurType)tracks.get(trackKeys[rows.get(rows.size()-1)]);
				track = updateTrack(track, random, rgKey, simpleformat, latmin, latmax, lonmin, lonmax);
				trackUpdates.put(track.get_id(), track);

				System.out.println("track-update, " + track.get_id());
			}

			int t = 0;
			for (String key : trackUpdates.keySet()) {
				track = (CotMinotaurType)trackUpdates.get(key);
				result.append(((t == 0) ? "" : ",") + track.toString());
				
				t++;
			}

			result.append("],\"removed\": [");
			for (int i = 0; i < removeList.size(); i++) {
				result.append(((i == 0) ? "" : ",") + "\"" + removeList.get(i) + "\"");
			}
			
			currentTime = new Date();
			result.append("], \"totalFeatures\": \"unknown\",\"numberReturned\": " + trackUpdates.size() + ",\"timeStamp\": \"" + 
					simpleformat.format(currentTime.getTime()) + "\","
					+ "\"crs\": {\"type\": \"name\",\"properties\": {\"name\": \"urn:ogc:def:crs:EPSG::4326\"}}}");

			setResource("size_" + ip, tracks.size());
			setResource("tracks_" + ip, tracks);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		
		return result.toString();
	}
	
	private CotMinotaurType updateTrack(CotMinotaurType track, RandomDataGenerator random, RandomGenerator rgKey, SimpleDateFormat simpleformat,
			Double latmin, Double latmax, Double lonmin, Double lonmax) {
		Date currentTime;

		String speed, altitude, threat = "UNK";
		int iOffset, nOffset = 0, nMultiplier = 10;
		
		track.movePoint();

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
		track.set_threat(threat);
		
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
		track.set_speed(Double.valueOf(speed));

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
		track.set_altitude(Double.valueOf(altitude));

		currentTime = new Date();
		track.set_date(simpleformat.format(currentTime.getTime()));

		return track;
	}

	private void showExecutionTime(String name, long start, long end) {
		long duration = (end - start);
		
		System.out.println("exectime (" + name + ") / secs (" + TimeUnit.NANOSECONDS.toSeconds(duration) + ")");
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

	public void showBaseUrl() {
	    String scheme = request.getScheme() + "://";
	    String serverName = request.getServerName();
	    String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort();
	    String contextPath = request.getContextPath();
	    
	    String realPath = servlet.getRealPath(".");
	    
	    System.out.println(scheme + "," + serverName + "," + serverPort + "," + contextPath + "," + realPath);
	  }
}