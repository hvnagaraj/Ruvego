package com.ruvego.project.server;



import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.bson.types.ObjectId;


import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.ruvego.project.client.CategoryPacket;
import com.ruvego.project.client.LoginModule;
import com.ruvego.project.client.ResultsBriefPanelPacket;
import com.ruvego.project.client.ResultsFetch;
import com.ruvego.project.client.ResultsPacket;
import com.ruvego.project.shared.ItineraryDataPacket;

@SuppressWarnings("serial")
public class RuvegoBackend extends RemoteServiceServlet implements ResultsFetch {

	/* Constants */
	String DATABASE = "testing_phase";
	public static final int BIN_DAYTIME = 1;
	public static final int BIN_NIGHTLIFE = 2;
	public static final int BIN_BOTH = 3;

	int MAX_VAR_COLS = 3;

	Mongo database;
	DB db;
	DBCollection coll;
	BasicDBObject query;
	DBCursor data;
	BasicDBObject dataObject;
	Object value;


	@Override
	public ResultsPacket[] fetchResults(String place, String withinMiles, int timeOfTheDay, String prevType, String request) {

		System.out.println("Place : " + place + " within : " + withinMiles + " timeOfTheDay : " + timeOfTheDay + " RequestType : "
				+ request + " prevType : " + prevType);

		BasicDBObject queryHeader = new BasicDBObject();
		BasicDBObject queryContent = new BasicDBObject();

		DBCursor header, content;

		String nextType = null;
		String[] result = null;
		String[] headerResult = null;

		/* Query Header collection to get the header information which is required for populating the Grid header */
		connectDB(prevType);
		queryHeader.put("name", request);
		header = coll.find(queryHeader);

		byte loopCount = 0;
		int numCols = 0;
		if (header.hasNext()) {
			loopCount++;

			assert(loopCount == 1);

			DBObject headerObject = header.next();

			numCols = (Integer) headerObject.get("numcols");

			/* Construct the array after the No of Cols is determined. This loop should run just once */
			headerResult = new String[numCols];
			result = new String[numCols];

			nextType = (String) headerObject.get("next");
			headerResult = getHeader(numCols, headerObject);
			System.out.println("Server: Header Found. No of Cols = " + numCols);
		}

		assert(headerResult != null);
		assert(result != null);

		/* Prepare the Result Content */
		connectDB(nextType);		

		/* Extracting miles from the string passed to this func. The string passed has the format "25 miles" */
		int milesInt = Integer.parseInt(withinMiles.substring(0, 3).replaceAll(" ", "")); 
		System.out.println("Dist in integer : " + milesInt);
		queryContent.put(place, new BasicDBObject().append("$lte", milesInt));

		if (timeOfTheDay == BIN_DAYTIME) {
			queryContent.put("timeoftheday", new BasicDBObject().append("$ne", BIN_NIGHTLIFE));
		} else if (timeOfTheDay == BIN_NIGHTLIFE) {
			queryContent.put("timeoftheday", new BasicDBObject().append("$ne", BIN_DAYTIME));
		} else {
			queryContent.put("timeoftheday", new BasicDBObject().append("$lte", timeOfTheDay));
		}

		queryContent.append(prevType, request);

		content = coll.find(queryContent);
		int numRows = content.count();

		System.out.println("No of elements : " + numRows);

		ResultsPacket[] packet = new ResultsPacket[numRows + 1];

		/* Construct the Header packet. This packet has the header information for the Grid table */
		packet[0] = new ResultsPacket(headerResult, numCols, numRows);

		/* Fill the content part. 0th element is the header. So start from 1 */
		int i = 1;
		while(content.hasNext()) {
			DBObject contentObject = content.next();

			result = getResultArray(numCols, contentObject, place);
			packet[i] = new ResultsPacket(result, numCols, (String)contentObject.get("next"));
			i++;
		}

		return packet;
	}

	private String[] getResultArray(int numCols, DBObject tempCur, String place) {
		String[] result = new String[MAX_VAR_COLS + 4];
		BasicDBObject temp;

		for (int i = 0; i < numCols; i++) {
			switch (i) {
			case 6:
				result[6] = (String) tempCur.get("col2");
			case 5:
				result[5] = (String) tempCur.get("col1");
			case 4:
				result[4] = (String) tempCur.get("col0");
			case 3:
				result[3] = (String) tempCur.get("rating");
			case 2:
				temp = (BasicDBObject) tempCur.get("duration");
				result[2] = (String) temp.getString(place);
			case 1:
				temp = (BasicDBObject) tempCur.get("distance");
				result[1] = (String) temp.getString(place);
			case 0:
				result[0] = (String) tempCur.get("name");
				break;
			default:
				break;
			}
		}
		return result;
	}

	private String[] getHeader(int numCols, DBObject tempCur) {
		String[] result = new String[MAX_VAR_COLS + 4];

		for (int i = 0; i < numCols; i++) {
			switch (i) {
			case 6:
				result[6] = (String) tempCur.get("col2");
			case 5:
				result[5] = (String) tempCur.get("col1");
			case 4:
				result[4] = (String) tempCur.get("col0");
			case 3:
				result[3] = "Rating";
			case 2:
				result[2] = "Travel Time";
			case 1:
				result[1] = "Distance";
			case 0:
				result[0] = (String) tempCur.get("next");
				break;
			default:
				break;
			}
		}
		return result;
	}

	@Override
	public String[] fetchCategoryColumns(String category, String categoryType) {
		String[] return_data = new String[MAX_VAR_COLS];

		System.out.println("Category type : " + categoryType + "  Category : " + category);
		connectDB(categoryType);

		BasicDBObject query = new BasicDBObject();
		query.put("name", category);

		DBCursor data = coll.find(query);
		while(data.hasNext()) {
			DBObject content = data.next();
			if (content != null) {
				for (int i = 0; i < MAX_VAR_COLS; i++) {
					return_data[i] = (String) content.get("col"+i);
				}
			}
		}
		return return_data;
	}

	@Override
	public String[] fetchCategoryList() {
		connectDB("Category");

		BasicDBObject query = new BasicDBObject();

		DBCursor data = coll.find(query);
		String[] return_data = new String[data.count()];

		int count = 0;
		while(data.hasNext()) {
			return_data[count] = (String) data.next().get("name");
			count++;
		}
		System.out.println("Returning Category List");
		return return_data;
	}

	//TODO revisit
	public String[] fetchTypeOptions(String categoryName, String categoryType) {
		connectDB(categoryType);

		BasicDBObject query = new BasicDBObject();
		query.put("name", categoryName);

		DBCursor data = coll.find(query);

		ArrayList<String> list = new ArrayList<String>();
		//BasicDBList list = new BasicDBList();
		while(data.hasNext()) {
			list.addAll((ArrayList<String>) data.next().get("typeOptions"));
		}

		String[] return_data = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			return_data[i] = list.get(i);	
		}

		System.out.println("Returning Type Options");
		return return_data;
	}

	@Override
	public String[] fetchSubCategoryList(String itemText) {
		connectDB("SubCategory");

		DBCursor data = coll.find();
		String[] return_data = new String[data.count()];

		int count = 0;
		while(data.hasNext()) {
			return_data[count] = (String) data.next().get("name");
			count++;
		}
		System.out.println("Server : Returning Sub Category List");
		return return_data;
	}

	@Override
	public String[] fetchPlaceList() {
		LinkedList<String> placeLL = new LinkedList<String>();
		String[] placeList;
		connectDB("Place");

		data = coll.find();
		while(data.hasNext()) {
			placeLL.add((String) data.next().get("name"));
		}

		placeList = new String[placeLL.size()];

		/* Linked List to Array */
		Iterator<String> list;

		list = placeLL.iterator();

		int i = 0;
		while (list.hasNext()) {
			placeList[i] = list.next();
			i++;
		}

		System.out.println("Server: No of Places added = " + placeList.length);
		return placeList;
	}

	/** Connect DB connects to the collection of the type passed */
	private void connectDB(String collection) {
		try {
			if (database == null) {
				database = new Mongo("localhost");
				db = database.getDB(DATABASE);
			}

			/* Collection will be named after Place, Category, Activity or Sub-Activity */
			coll = db.getCollection(collection);
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public CategoryPacket fetchCategoryResults(String place,
			String withinMiles, int timeOfTheDayType) {

		connectDB("Category");

		return getCategoryData(place, withinMiles, timeOfTheDayType);
	}

	private CategoryPacket getCategoryData(String place, String withinMiles,
			int timeOfTheDay) {
		String delims;
		String[] tokens;

		System.out.println("Server Category Query : Place - " + place + " Within Range - " + withinMiles + " Time of the day - " + timeOfTheDay);

		/* Query */
		query = new BasicDBObject();

		/* Removing miles from the string passed to this func */
		delims = " miles";
		tokens = withinMiles.split(delims);
		query.put(place, new BasicDBObject().append("$lte", Integer.parseInt(tokens[0])));

		if (timeOfTheDay == BIN_DAYTIME) {
			query.put("timeoftheday", new BasicDBObject().append("$ne", BIN_NIGHTLIFE));
		} else if (timeOfTheDay == BIN_NIGHTLIFE) {
			query.put("timeoftheday", new BasicDBObject().append("$ne", BIN_DAYTIME));
		} else {
			query.put("timeoftheday", new BasicDBObject().append("$lte", timeOfTheDay));
		}

		DBCursor content = coll.find(query);
		CategoryPacket returnData = new CategoryPacket(content.count());

		int elemNum = 0;
		while(content.hasNext()) {
			DBObject data = content.next();

			returnData.setImageCaption(elemNum, (String) data.get("name"));
			returnData.setImagePath(elemNum, (String) data.get("imagepath"));
			returnData.setNext(elemNum, (String) data.get("next"));
			returnData.setPrev(elemNum, (String) data.get("prev"));
			elemNum++;
		}
		System.out.println("Server : No of elements = " + elemNum);
		System.out.println("Server : Returning Category Packet");

		return returnData;	
	}

	@Override
	public CategoryPacket fetchSubcategoryResults(String place,
			String withinMiles, int timeOfTheDayType) {
		connectDB("SubCategory");

		return getCategoryData(place, withinMiles, timeOfTheDayType);
	}

	@Override
	public ResultsBriefPanelPacket fetchBriefPanelResults(String name) {

		ResultsBriefPanelPacket packet = null;

		connectDB("Activity");

		/* Query */
		query = new BasicDBObject();
		query.put("name", name);

		DBCursor content = coll.find(query);

		while(content.hasNext()) {
			DBObject data = content.next();

			String id = data.get("_id").toString();
			System.out.println("id : " + id);
			packet = new ResultsBriefPanelPacket(name, (String) data.get("address"), (String) data.get("brief"), 
					(String) data.get("website"), (String) data.get("rating"), (String) data.get("imagepath"), 
					(String) data.get("miscinfo"), (String) data.get("timings"), (String) data.get("contact"), id);
			System.out.println("Server Image path : " + (String) data.get("imagepath"));
			System.out.println("Contact : " + (String) data.get("contact"));
		}


		return packet;
	}

	@Override
	public boolean authenticateUser(String username, String password) {
		connectDB("authenticatedusers");

		/* Query */
		query = new BasicDBObject();
		query.put("username", username);
		query.put("password", password);

		DBCursor content = coll.find(query);

		while(content.hasNext()) {
			return true;
		}
		return false;
	}

	@Override
	public ItineraryDataPacket fetchItineraryData(String itineraryName, String username) {
		ItineraryDataPacket dataPacket = null;
		connectDB("Itinerary");

		/* Query */
		query = new BasicDBObject();
		query.put("name", itineraryName);
		query.put("users", username);

		DBCursor content = coll.find(query);

		while(content.hasNext()) {
			DBObject data = content.next();

			int numDays = Integer.parseInt((String) data.get("numdays"));
			dataPacket = new ItineraryDataPacket(itineraryName, numDays, (String)data.get("startdate"), 0);

			String[] temp = new String[2];
			for (int i = 0; i < numDays; i++) {
				BasicDBList activityList = (BasicDBList) data.get("Day " + (i + 1));
				
				if (activityList == null || activityList.size() == 0) {
					dataPacket.setData(i, 0, null, null, null);
					continue;
				}

				String[] nameList = new String[activityList.size()];
				String[] addressList = new String[activityList.size()];
				String[] objectIdList = new String[activityList.size()];
				for (int j = 0; j < activityList.size(); j++) {
					temp = getActivityNameAddress(activityList.get(j), (i + 1));
					nameList[j] = temp[0];	
					addressList[j] = temp[1];
					objectIdList[j] = temp[2];
				}
				dataPacket.setData(i, activityList.size(), nameList, addressList, objectIdList);
			}
		}
		
		if (dataPacket == null) {
			dataPacket = new ItineraryDataPacket(1);
		}
		return dataPacket;
	}

	protected String[] getActivityNameAddress(Object object, int day) {
		String[] nameAddressID = new String[3];

		connectDB("Activity");

		/* Query */
		query = new BasicDBObject();
		query.put("_id", object);

		DBCursor content = coll.find(query);

		while(content.hasNext()) {
			DBObject data = content.next();

			nameAddressID[0] = (String) data.get("name");
			nameAddressID[1] = (String) data.get("address");
			nameAddressID[2] = (String) data.get("Day " + day);
		}

		return nameAddressID;
	}
	

}

