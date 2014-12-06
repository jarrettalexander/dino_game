package edu.uark.csce.mobile.dinogame;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SimpleGeofenceStore {
	
	// Local SQLite Database info
	private SQLiteDatabase db;
	private LocationSQLiteHelper dbHelper;
	private String[] allColumns = { LocationSQLiteHelper.LOCATIONS_COLUMN_ID, LocationSQLiteHelper.LOCATIONS_COLUMN_LATITUDE, 
									LocationSQLiteHelper.LOCATIONS_COLUMN_LONGITUDE, LocationSQLiteHelper.LOCATIONS_COLUMN_RADIUS, 
									LocationSQLiteHelper.LOCATIONS_COLUMN_DURATION, LocationSQLiteHelper.LOCATIONS_COLUMN_TRANSITION_TYPE,
									LocationSQLiteHelper.LOCATIONS_COLUMN_ITEM_ID, LocationSQLiteHelper.LOCATIONS_COLUMN_COMPLETED };
	
	
	// The SharedPreferences object in which geofence info is stored
    private final SharedPreferences mPrefs;

    // The name of the resulting SharedPreferences
    private static final String SHARED_PREFERENCE_NAME = MapActivity.class.getSimpleName();

    // Create the SharedPreferences storage with private access only
    public SimpleGeofenceStore(Context context) {
    	dbHelper = new LocationSQLiteHelper(context);
        mPrefs = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }
    
	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
    /**
     * Create a Geofence record if the ID is unique
	 *
     * @param geofence The {@link SimpleGeofence} containing the
     * values you want to save in the local database
     */
    public SimpleGeofence createGeofence(SimpleGeofence geo) {
    	
    	//saveSimpleGeofenceToSharedPreferences(geo);
    	    	
    	Cursor alreadyPresent = db.query(LocationSQLiteHelper.LOCATIONS_TABLE_NAME, allColumns, LocationSQLiteHelper.LOCATIONS_COLUMN_ID + " = " + geo.getId(), null, null, null, null);
    	if(alreadyPresent.getCount() <= 0) {
    	
	    	ContentValues  values = new ContentValues();
			values.put(LocationSQLiteHelper.LOCATIONS_COLUMN_ID, geo.getId());
			values.put(LocationSQLiteHelper.LOCATIONS_COLUMN_LATITUDE, geo.getLatitude());
			values.put(LocationSQLiteHelper.LOCATIONS_COLUMN_LONGITUDE, geo.getLongitude());
			values.put(LocationSQLiteHelper.LOCATIONS_COLUMN_RADIUS, geo.getRadius());
			values.put(LocationSQLiteHelper.LOCATIONS_COLUMN_DURATION, geo.getExpirationDuration());
			values.put(LocationSQLiteHelper.LOCATIONS_COLUMN_TRANSITION_TYPE, geo.getTransitionType());
			values.put(LocationSQLiteHelper.LOCATIONS_COLUMN_ITEM_ID, geo.getItemId());
			values.put(LocationSQLiteHelper.LOCATIONS_COLUMN_COMPLETED, geo.getCompleted());
			
			long insertId = db.insert(LocationSQLiteHelper.LOCATIONS_TABLE_NAME, null, values);
			Cursor cursor = db.query(LocationSQLiteHelper.LOCATIONS_TABLE_NAME, allColumns, LocationSQLiteHelper.LOCATIONS_COLUMN_ID + " = " + insertId, null, null, null, null);
			cursor.moveToFirst();
			SimpleGeofence geofence = cursorToSimpleGeofence(cursor);
			cursor.close();
			
			return geofence;
			
    	} else {
    		return null;
    	}
    }
    
    /**
     * Returns a stored geofence by its id, or returns {@code null}
     * if it's not found.
     *
     * @param id The ID of a stored geofence
     * @return A geofence defined by its center and radius. See
     * {@link SimpleGeofence}
     */
    public SimpleGeofence getGeofenceById(String id) {
    	
    	//return getSimpleGeofenceFromSharedPreferences(id);
    	
		String query = "SELECT * FROM " + LocationSQLiteHelper.LOCATIONS_TABLE_NAME + " WHERE " + LocationSQLiteHelper.LOCATIONS_COLUMN_ID + " = " + id;
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		if(cursor.getCount() > 0) {
			SimpleGeofence geo = cursorToSimpleGeofence(cursor);
			return geo;
		} else {
			return null;
		}
    }
    
    /**
     * Returns all SimpleGeofence objects stored in the locations database
     */
    public ArrayList<SimpleGeofence> getAllGeofences() {
    	ArrayList<SimpleGeofence> geofences = new ArrayList<SimpleGeofence>();
		
		Cursor cursor = db.query(LocationSQLiteHelper.LOCATIONS_TABLE_NAME, allColumns, null, null, null, null, LocationSQLiteHelper.LOCATIONS_COLUMN_ID + " DESC");
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			SimpleGeofence geofence = cursorToSimpleGeofence(cursor);
			geofences.add(geofence);
			cursor.moveToNext();
		}
		
		cursor.close();
		return geofences;
    }
    
    /**
     * Sets the Completed flag to True for the Geofence in the local database with the associated ID.
     * 
     * @param id The ID of the geofence to set Completed to true
     */
    public void setLocationToCompleted(String id) {
    	String where = "_id=" + id;
    	ContentValues args = new ContentValues();
    	args.put(LocationSQLiteHelper.LOCATIONS_COLUMN_COMPLETED, true);
    	db.update(LocationSQLiteHelper.LOCATIONS_TABLE_NAME, args, where, null);
    }
    
    // Convert cursor to SimpleGeofence object
 	private SimpleGeofence cursorToSimpleGeofence(Cursor cursor) {
 		
 		String id = cursor.getString(0);
 		double latitude = cursor.getDouble(1);
 		double longitude = cursor.getDouble(2);
 		float radius = cursor.getFloat(3);
 		long duration = cursor.getLong(4);
 		int transition = cursor.getInt(5);
 		long item = cursor.getLong(6);
 		boolean completed = cursor.getInt(7) > 0;
 		
 		return new SimpleGeofence(id, latitude, longitude, radius, duration, transition, item, completed);
 	}
 	
 	/*
 	 * Below are methods associated with saving and retrieving SimpleGeofences from SharedPreferences.
 	 * These methods are no longer used.
 	 */

// 	/**
//     * Using the ID of the Geofence the user last entered, finds the next ID in order and returns the
//     * Geofence associated with that ID.
//     * 
//     * @return A Geofence defined by its center and radius. See
//     * {@link SimpleGeofence}
//     */
//    public SimpleGeofence getCurrentGeofence() {
//    	
//    	SimpleGeofence currentGeofence;
//    	
//    	// Get ID of last Geofence the user entered
//    	String lastGeofenceReceived = mPrefs.getString(GeofenceUtils.KEY_LAST_GEOFENCE_ID, null);
//    	
//    	// Get the array of IDs of Geofences stored in SharedPreferences
//    	int size = mPrefs.getInt(GeofenceUtils.KEY_GEOFENCE_ID_ARRAY + "_size", 0);  
//        String geofenceIds[] = new String[size];  
//        for(int i=0;i<size;i++)  
//            geofenceIds[i] = mPrefs.getString(GeofenceUtils.KEY_GEOFENCE_ID_ARRAY + "_" + i, null);  
//    	
//        // Get ID just above the last ID the user entered
//        Arrays.sort(geofenceIds);
//        int indexOfLastGeofenceReceived = (lastGeofenceReceived != null) ? Arrays.asList(geofenceIds).indexOf(lastGeofenceReceived) : -1;
//        int indexOfGeofenceToRetrieve = indexOfLastGeofenceReceived + 1;
//        
//        if(indexOfGeofenceToRetrieve < size) {
//        	String idOfGeofenceToRetrieve = geofenceIds[indexOfGeofenceToRetrieve];
//        	currentGeofence = getGeofenceById(idOfGeofenceToRetrieve);
//        } else {
//        	currentGeofence = getGeofenceById(lastGeofenceReceived);
//        }
//        
//        return currentGeofence;
//    	
//    }
//    
//
//    // Updates the value of the ID of the last geofence the user entered
//    public void incrementLastGeofenceReceived(String id) {
//    	Editor editor = mPrefs.edit();
//    	editor.putString(GeofenceUtils.KEY_LAST_GEOFENCE_ID, id);
//    	editor.commit();
//    }
//    
//    /**
//     * Given a Geofence object's ID and the name of a field
//     * (for example, KEY_LATITUDE), return the key name of the
//     * object's values in SharedPreferences.
//     *
//     * @param id The ID of a Geofence object
//     * @param fieldName The field represented by the key
//     * @return The full key name of a value in SharedPreferences
//     */
//    private String getGeofenceFieldKey(String id,
//            String fieldName) {
//        return GeofenceUtils.KEY_PREFIX + "_" + id + "_" + fieldName;
//    }
//    
//    // Saves a SimpleGeofence object to a SharedPreferences file
//    private void saveSimpleGeofenceToSharedPreferences(SimpleGeofence geofence) {
//    	
//    	String id = geofence.getId();
//    	Editor editor = mPrefs.edit();
//        
//        // Be sure to add the ID to the list of Geofence IDs stored
//    	int size = mPrefs.getInt(GeofenceUtils.KEY_GEOFENCE_ID_ARRAY + "_size", 0);
//    	editor.putInt(GeofenceUtils.KEY_GEOFENCE_ID_ARRAY + "_size", size + 1);    	
//    	editor.putString(GeofenceUtils.KEY_GEOFENCE_ID_ARRAY + "_" + size, geofence.getId());
//
//        // Write the Geofence values to SharedPreferences
//        editor.putFloat(
//                getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE),
//                (float) geofence.getLatitude());
//
//        editor.putFloat(
//                getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE),
//                (float) geofence.getLongitude());
//
//        editor.putFloat(
//                getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS),
//                geofence.getRadius());
//
//        editor.putLong(
//                getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION),
//                geofence.getExpirationDuration());
//
//        editor.putInt(
//                getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE),
//                geofence.getTransitionType());
//
//        // Commit the changes
//        editor.commit();
//    }
//    
//    // Get a SimpleGeofence from SharedPreferences based on ID
//    private SimpleGeofence getSimpleGeofenceFromSharedPreferences(String id) {
//    	
//    	/*
//         * Get the latitude for the geofence identified by id, or GeofenceUtils.INVALID_VALUE
//         * if it doesn't exist
//         */
//        double lat = mPrefs.getFloat(
//                getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE),
//                GeofenceUtils.INVALID_FLOAT_VALUE);
//
//        /*
//         * Get the longitude for the geofence identified by id, or
//         * GeofenceUtils.INVALID_VALUE if it doesn't exist
//         */
//        double lng = mPrefs.getFloat(
//                getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE),
//                GeofenceUtils.INVALID_FLOAT_VALUE);
//
//        /*
//         * Get the radius for the geofence identified by id, or GeofenceUtils.INVALID_VALUE
//         * if it doesn't exist
//         */
//        float radius = mPrefs.getFloat(
//                getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS),
//                GeofenceUtils.INVALID_FLOAT_VALUE);
//
//        /*
//         * Get the expiration duration for the geofence identified by
//         * id, or GeofenceUtils.INVALID_VALUE if it doesn't exist
//         */
//        long expirationDuration = mPrefs.getLong(
//                getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION),
//                GeofenceUtils.INVALID_LONG_VALUE);
//
//        /*
//         * Get the transition type for the geofence identified by
//         * id, or GeofenceUtils.INVALID_VALUE if it doesn't exist
//         */
//        int transitionType = mPrefs.getInt(
//                getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE),
//                GeofenceUtils.INVALID_INT_VALUE);
//        
//        /*
//         * Get the item id for the geofence identified by id
//         */
//        long itemId = mPrefs.getLong(
//        		getGeofenceFieldKey(id, GeofenceUtils.KEY_ITEM_ID),
//        		GeofenceUtils.INVALID_LONG_VALUE);
//        
//        /*
//         * Get the completed value for the geofence identified by id
//         */
//        int completed = mPrefs.getInt(
//        		getGeofenceFieldKey(id, GeofenceUtils.KEY_COMPLETED),
//        		GeofenceUtils.INVALID_INT_VALUE);
//
//        // If none of the values is incorrect, return the object
//        if (
//            lat != GeofenceUtils.INVALID_FLOAT_VALUE &&
//            lng != GeofenceUtils.INVALID_FLOAT_VALUE &&
//            radius != GeofenceUtils.INVALID_FLOAT_VALUE &&
//            expirationDuration != GeofenceUtils.INVALID_LONG_VALUE &&
//            transitionType != GeofenceUtils.INVALID_INT_VALUE &&
//            itemId != GeofenceUtils.INVALID_LONG_VALUE &&
//            completed != GeofenceUtils.INVALID_INT_VALUE) {
//
//            // Return a true Geofence object
//            return new SimpleGeofence(id, lat, lng, radius, expirationDuration, transitionType, itemId, completed > 0);
//
//        // Otherwise, return null.
//        } else {
//            return null;
//        }
//    }
}
