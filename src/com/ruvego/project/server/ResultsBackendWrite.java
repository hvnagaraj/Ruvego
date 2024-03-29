package com.ruvego.project.server;

import java.io.BufferedReader;
import java.lang.Math;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.streetview.PhotoSpec;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.ruvego.project.client.ResultsWrite;
import com.ruvego.project.client.WriteActivityPacket;
import com.ruvego.project.client.WriteCategoryPacket;
import com.ruvego.project.client.WritePlacePacket;
import com.ruvego.project.shared.CreateItineraryPacket;

@SuppressWarnings("serial")
public class ResultsBackendWrite extends RemoteServiceServlet implements ResultsWrite {

	/* Constants */
	String DATABASE = "testing_phase";
	public static final int BIN_DAYTIME = 1;
	public static final int BIN_NIGHTLIFE = 2;
	public static final int BIN_BOTH = 3;

	LatLng place1 = null, place2;

	private Mongo database;
	private DB db;

	public boolean writeResults(WritePlacePacket writeData) {
		/* Header query */
		BasicDBObject query = new BasicDBObject();
		query.put("place", writeData.getPlaceName());


		if (connectDBAndCheckForEntry("Place", query) == false) {
			return false;
		}

		BasicDBObject dataObject = new BasicDBObject();
		Object value = new Object();

		value = writeData.getPlaceName() + ", " + writeData.getState();

		dataObject.put("name", value);
		dataObject.append("next", "Category");
		dataObject.append("lon", writeData.getLon());
		dataObject.append("lat", writeData.getLat());

		if (connectDBAndSave("Place", dataObject) == false) {
			return false;
		}

		System.out.println("Successfully inserted Place entry into DB");

		return true;		
	}

	private boolean connectDBAndUpdate(String collectionName, BasicDBObject getObject, BasicDBObject updateObject) {
		DBCollection coll = null;

		try {
			if (database == null) {
				database = new Mongo("localhost");
				db = database.getDB(DATABASE);
			}

			/* Collection will be named after Place, Category, Activity or Sub-Activity */
			coll = db.getCollection(collectionName);
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		coll.update(getObject, updateObject);

		return true;
	}

	private boolean connectDBAndSave(String collectionName, BasicDBObject dataObject) {
		DBCollection coll = null;

		try {
			if (database == null) {
				database = new Mongo("localhost");
				db = database.getDB(DATABASE);
			}

			/* Collection will be named after Place, Category, Activity or Sub-Activity */
			coll = db.getCollection(collectionName);
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		coll.save(dataObject);

		return true;
	}

	private boolean connectDBAndCheckForEntry(String collectionName, DBObject query) {
		DBCollection coll = null;

		try {
			if (database == null) {
				database = new Mongo("localhost");
				db = database.getDB(DATABASE);
			}

			/* Collection will be named after Place, Category, Activity or Sub-Activity */
			coll = db.getCollection(collectionName);
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		DBCursor data = coll.find(query);
		while(data.hasNext()) {
			if (data.next() != null) {
				System.err.println("Entry already present. Cannot overwrite");
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean writeResults(WriteCategoryPacket writeData) {
		LinkedList<String> typeOptions = null;

		/* Header query */
		BasicDBObject query = new BasicDBObject();
		query.put("name", writeData.getName());

		if (writeData.getCategory() != null) {
			if (connectDBAndCheckForEntry("SubCategory", query) == false) {
				return false;
			}
		} else {
			if (connectDBAndCheckForEntry("Category", query) == false) {
				return false;
			}			
		}

		BasicDBObject dataObject = new BasicDBObject();
		Object value = new Object();

		dataObject.append("name", writeData.getName());
		dataObject.append("imagepath", "icons/" + writeData.getImagePath());

		int count = 0;
		for (int i = 0; i < writeData.MAX_VAR_COLS; i++) {
			value = writeData.getCol(i);
			if (value.equals("")) {
				break;
			}
			dataObject.append("col" + i, value);
			count++;
		}

		if (writeData.getCol(0).equalsIgnoreCase("subcategory")) {
			dataObject.append("next", "SubCategory");	
			dataObject.append("numcols", 1);
		} else {
			dataObject.append("next", "Activity");
			dataObject.append("numcols", count + 4);
		}

		if (writeData.getCategory() != null) {
			dataObject.append("category", writeData.getCategory());
		}

		/* Type options */
		Iterator<String> list;
		typeOptions = writeData.getTypeOptions();
		String[] listArray = new String[typeOptions.size()];

		list = typeOptions.iterator();

		int i = 0;
		while (list.hasNext()) {
			listArray[i] = list.next();
			i++;
		}

		if (i != 0) {
			dataObject.append("typeOptions", listArray);
		}

		if (writeData.getCategory() != null) {
			connectDBAndSave("SubCategory", dataObject);
		} else {
			connectDBAndSave("Category", dataObject);
		}

		System.out.println("Server : Successfully inserted Category entry into DB");

		return true;
	}

	public boolean writeResults(WriteActivityPacket writeData) {
		/* Header query */
		BasicDBObject query = new BasicDBObject();
		query.put("name", writeData.getName());

		if (connectDBAndCheckForEntry(writeData.getType(), query) == false) {
			return false;
		}

		BasicDBObject dataObject = new BasicDBObject();
		Object value = new Object();

		dataObject.append("name", writeData.getName());
		dataObject.append("brief", writeData.getBrief());
		dataObject.append("website", writeData.getWebsite());
		dataObject.append("rating", writeData.getRating());
		dataObject.append("imagepath", "icons/" + writeData.getImagePath());
		dataObject.append("timeoftheday", writeData.getcheckBoxValue());
		dataObject.append("contact", writeData.getContact());

		if (writeData.getSubCategory() != null) {
			dataObject.append("subcategory", writeData.getSubCategory());
		}

		for (int i = 0; i < writeData.MAX_VAR_COLS; i++) {
			value = writeData.getCol(i);
			if (!value.equals("")) { 
				dataObject.append("col"+i, value);
			}
		}

		String infoData = "";
		if (writeData.getEntryFee().equalsIgnoreCase("")) {
			infoData = infoData + "Entry Fee/Ticket : 0$";
		} else {
			infoData = infoData + "Entry Fee/Ticket : " + writeData.getEntryFee();
		}

		if (!writeData.getToll().equalsIgnoreCase("")) {
			infoData = infoData + "<BR>Toll : " + writeData.getToll(); 
		}

		if (!writeData.getParking().equalsIgnoreCase("")) {
			infoData = infoData + "<BR>Parking : " + writeData.getParking();
		}

		dataObject.append("miscinfo", infoData);
		dataObject.append("timings", writeData.getTimings());
		if (writeData.getSubCategory() == null) {
			dataObject.append("Category", writeData.getCategory());
		} else {
			dataObject.append("SubCategory", writeData.getSubCategory());
		}

		dataObject.append("next", "");

		/* Get all the places within the 150 miles range */
		String[] placeList;
		placeList = getPlaceWithinBounds(writeData);

		/* Maps */
		Integer[] dist = new Integer[placeList.length];
		String distContent;
		URL distanceMatrix;
		String source = writeData.getAddress();
		String destinations = "";
		try {
			source = source.replaceAll(" ", "+");
			source = source.replaceAll("\n", "");
			System.out.println(source);

			destinations = StringUtils.join(placeList, "|");
			destinations = destinations.replaceAll(" ", "+");
			System.out.println(destinations);

			distanceMatrix = new URL("http://maps.googleapis.com/maps/api/distancematrix/json?origins="+ String.valueOf(source) +
					"&destinations=" + destinations + "&mode=driving&sensor=false&units=imperial");
			URLConnection distance = distanceMatrix.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(distance.getInputStream()));

			String inputLine;
			int distCount = 0, durCount = 0;
			BasicDBObject distDBObject = new BasicDBObject();
			BasicDBObject durDBObject = new BasicDBObject();
			while ((inputLine = in.readLine()) != null) {
				String delims;
				String[] tokens;
				String[] contentTokens;  

				if (inputLine.contains("origin_addresses")) {

					delims = " \"";
					tokens = inputLine.split(delims);

					delims = "\" ]";
					contentTokens = tokens[2].split(delims);

					if (contentTokens[0].equalsIgnoreCase("")) {
						return false;
					}

					dataObject.append("address", contentTokens[0]);
				}

				if (inputLine.contains("distance")) {

					inputLine = in.readLine();
					delims = "\"text\" : \"";
					tokens = inputLine.split(delims);

					if (inputLine.contains("ft")) {
						dist[distCount] = 0;
					} else {
						delims = " mi";
						contentTokens = tokens[1].split(delims);
						dist[distCount] = (int) Double.parseDouble(contentTokens[0]);
					}

					delims = "\",";
					contentTokens = tokens[1].split(delims);
					distContent = contentTokens[0];

					distDBObject.append(placeList[distCount], distContent);

					dataObject.append("distance", distDBObject);

					System.out.println("Distance for place - " + placeList[distCount] + " : " + dist[distCount]);
					System.out.println("Distance in string : " + contentTokens[0]);

					dataObject.append(placeList[distCount], ((dist[distCount] / 25) + 1) * 25);

					int timeOfTheDayValue = getTimeOfTheDay(writeData);
					timeOfTheDayValue = timeOfTheDayValue | writeData.getcheckBoxValue();

					/* Update Category */
					updateCategory(writeData, dist[distCount], "Category", placeList[distCount], timeOfTheDayValue, writeData.getCategory());

					/* Update SubCategory if applicable*/
					if (writeData.getSubCategory() != null) {
						updateCategory(writeData, dist[distCount], "SubCategory", placeList[distCount], timeOfTheDayValue, writeData.getSubCategory());
					}

					distCount++;
				}

				/* Calculate duration for the parent Place only */
				if (inputLine.contains("duration")) {

					inputLine = in.readLine();
					delims = "\"text\" : \"";
					tokens = inputLine.split(delims);

					delims = "\"";
					tokens = tokens[1].split(delims);

					durDBObject.append(placeList[durCount], tokens[0]);

					dataObject.append("duration", durDBObject);

					System.out.println("Duration : " + tokens[0]);

					durCount++;
				}

				assert(distCount == durCount);

				if (inputLine.contains("status")) {

					delims = "\"status\" :";
					tokens = inputLine.split(delims);

					delims = "\"";
					tokens = tokens[1].split(delims);

					System.out.println("Status : " + tokens[1]);

				}

			}
			in.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* Completed writing to the Activity Collection. Commit to DB */
		connectDBAndSave("Activity", dataObject);

		/* Update Place, Category and Sub Category */
		/* Header query to read the Time of the day contents first and then edit-write to DB */

		System.out.println("Server: Successfully inserted Activity entry into DB");

		return true;
	}

	private void updateCategory(WriteActivityPacket writeData, Integer dist, String categoryType, String place, int timeOfTheDayValue, String category) {
		BasicDBObject update = new BasicDBObject().append(place, ((dist / 25) + 1) * 25);

		/* Update Category and SubCategory */
		connectDBAndUpdate(categoryType, new BasicDBObject().append("name", category), new BasicDBObject().append("$addToSet", update));
		connectDBAndUpdate(categoryType, new BasicDBObject().append("name", category), new BasicDBObject().append("$set", 
				new BasicDBObject().append("timeoftheday", timeOfTheDayValue)));
		System.out.println("Updated : " + categoryType + " Place : " + place + " Dist : " + dist);
	}

	private int getTimeOfTheDay(WriteActivityPacket writeData) {
		int value = 0;

		DBCollection coll = null;

		try {
			if (database == null) {
				database = new Mongo("localhost");
				db = database.getDB(DATABASE);
			}

			/* Collection will be named after Place, Category, Activity or Sub-Activity */
			coll = db.getCollection("Category");
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		BasicDBObject query = new BasicDBObject();
		query.put("name", writeData.getCategory());

		DBCursor content = coll.find(query);

		while(content.hasNext()) {
			DBObject data = content.next();

			if (data.get("timeoftheday") != null) {
				value = (Integer) data.get("timeoftheday");
			}
		}
		return value;
	}

	private String[] getPlaceWithinBounds(WriteActivityPacket writeData) {
		DBCollection coll = null;

		try {
			if (database == null) {
				database = new Mongo("localhost");
				db = database.getDB(DATABASE);
			}

			/* Collection will be named after Place, Category, Activity or Sub-Activity */
			coll = db.getCollection("Place");
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* Query */
		BasicDBObject queryPlace = new BasicDBObject();
		queryPlace.put("lon", new BasicDBObject().append("$lte", writeData.getLonE()));
		queryPlace.put("lon", new BasicDBObject().append("$gte", writeData.getLonW()));
		queryPlace.put("lat", new BasicDBObject().append("$lte", writeData.getLatN()));

		if (writeData.getLatS() > 0) {
			queryPlace.put("lat", new BasicDBObject().append("$gte", writeData.getLatS()));
		}

		System.out.println(writeData.getLonE() + "   " + writeData.getLonW() + "   " + writeData.getLatN() + "   " + writeData.getLatS());

		DBCursor placeContent = coll.find(queryPlace);
		String[] placeList = new String[placeContent.count()];
		int elemNum = 0;
		while(placeContent.hasNext()) {
			DBObject data = placeContent.next();

			placeList[elemNum] = (String) data.get("name");
			System.out.println(placeList[elemNum]);
			elemNum++;
		}
		return placeList;
	}

	@Override
	public boolean writeCreateItinerary(CreateItineraryPacket createItineraryPacket) {
		BasicDBObject query = new BasicDBObject();
		query.put("name", createItineraryPacket.getItineraryName());
		query.append("creator", createItineraryPacket.getUsername());

		if (connectDBAndCheckForEntry("Itinerary", query) == false) {
			return false;
		}

		BasicDBObject dataObject = new BasicDBObject();

		dataObject.append("name", createItineraryPacket.getItineraryName());
		dataObject.append("numdays", createItineraryPacket.getNumDays());
		dataObject.append("startdate", createItineraryPacket.getStartDate());
		dataObject.append("creator", createItineraryPacket.getUsername());

		connectDBAndSave("Itinerary", dataObject);
		System.out.println("creator : " + createItineraryPacket.getUsername());
		connectDBAndUpdate("Itinerary", new BasicDBObject().append("name", createItineraryPacket.getItineraryName()), 
				new BasicDBObject().append("$addToSet", new BasicDBObject().append("users", createItineraryPacket.getUsername())));

		return true;
	}

	@Override
	public boolean addEntry(String itineraryName, String day, String objectId, String username) {
		/*		
		db.command("db.Itinerary.update( { name : " + itineraryName + ", " + day + ": { $nin : " + objectId + " } }, " +
				"{ votes : { $inc : 1 }, voters : { $push : \"calvin\" } );");

		 */
		BasicDBObject update = new BasicDBObject().append(day, new ObjectId(objectId));

		/* Update Category and SubCategory */
		connectDBAndUpdate("Itinerary", new BasicDBObject().append("name", itineraryName), new BasicDBObject().append("$addToSet", update));

		return true;
	}

	@Override
	public boolean addEntries(String itineraryName, String day,
			String[] objectIdList, String username) {
		System.out.println("Itinerary : " + itineraryName + " for " + day + " for user : " + username);
		BasicDBObject update = new BasicDBObject();
		System.out.println("Length : " + objectIdList.length);
		for (int i = 0; i < objectIdList.length; i++) {
			update.append("Day " + day, new ObjectId(objectIdList[i]));
			System.out.println("Id : " + objectIdList[i].toString());
		}

		/* Update Category and SubCategory */
		connectDBAndUpdate("Itinerary", new BasicDBObject().append("name", itineraryName), new BasicDBObject().append("$addToSet", update));

		return true;
	}

}
