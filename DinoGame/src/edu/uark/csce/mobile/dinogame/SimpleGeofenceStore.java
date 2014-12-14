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
import android.util.Log;

/**
 * Used to retrieve and store location information to the local database.
 * 
 * @author Jarrett Alexander
 *
 */
public class SimpleGeofenceStore {
	
	// Local SQLite Database info
	private SQLiteDatabase db;
	private LocationSQLiteHelper dbHelper;
	private String[] allColumns = { LocationSQLiteHelper.LOCATIONS_COLUMN_ID, LocationSQLiteHelper.LOCATIONS_COLUMN_LATITUDE, 
									LocationSQLiteHelper.LOCATIONS_COLUMN_LONGITUDE, LocationSQLiteHelper.LOCATIONS_COLUMN_RADIUS, 
									LocationSQLiteHelper.LOCATIONS_COLUMN_DURATION, LocationSQLiteHelper.LOCATIONS_COLUMN_TRANSITION_TYPE,
									LocationSQLiteHelper.LOCATIONS_COLUMN_ITEM_ID, LocationSQLiteHelper.LOCATIONS_COLUMN_COMPLETED };

    // Get LocationSQLiteHelper instance to open writable database
    public SimpleGeofenceStore(Context context) {
    	dbHelper = new LocationSQLiteHelper(context);
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

    	// Check if SimpleGeofence with requested ID is already in local storage
    	Cursor alreadyPresent = db.query(LocationSQLiteHelper.LOCATIONS_TABLE_NAME, allColumns, LocationSQLiteHelper.LOCATIONS_COLUMN_ID + " = " + geo.getId(), null, null, null, null);
    	Log.d("exists", String.valueOf(alreadyPresent.getCount()));
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
			
			// Return Geofence that was successfully inserted
			return geofence;
			
    	} else {
    		return null;
    	}
    }
    
    /**
     * Returns a stored Geofence by its ID, or returns {@code null}
     * if it's not found.
     *
     * @param id The ID of a stored Geofence
     * @return A Geofence defined by its center and radius. See
     * {@link SimpleGeofence}
     */
    public SimpleGeofence getGeofenceById(String id) {
    	
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
     * Returns all SimpleGeofence objects stored in the locations database that aren't marked as completed
     */
    public ArrayList<SimpleGeofence> getAllUncompletedGeofences() {
    	ArrayList<SimpleGeofence> geofences = new ArrayList<SimpleGeofence>();
		
		Cursor cursor = db.query(LocationSQLiteHelper.LOCATIONS_TABLE_NAME, allColumns, LocationSQLiteHelper.LOCATIONS_COLUMN_COMPLETED + " != 1", null, null, null, LocationSQLiteHelper.LOCATIONS_COLUMN_ID + " DESC");
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
     * @param id The ID of the Geofence to set Completed to true
     */
    public void setLocationToCompleted(String id) {
    	String where = "_id=" + id;
    	ContentValues args = new ContentValues();
    	args.put(LocationSQLiteHelper.LOCATIONS_COLUMN_COMPLETED, true);
    	db.update(LocationSQLiteHelper.LOCATIONS_TABLE_NAME, args, where, null);
    }
    
    // Convert cursor to SimpleGeofence object
 	private SimpleGeofence cursorToSimpleGeofence(Cursor cursor) {
 		
 		String id = cursor.getString(cursor.getColumnIndex(LocationSQLiteHelper.LOCATIONS_COLUMN_ID));
 		double latitude = cursor.getDouble(cursor.getColumnIndex(LocationSQLiteHelper.LOCATIONS_COLUMN_LATITUDE));
 		double longitude = cursor.getDouble(cursor.getColumnIndex(LocationSQLiteHelper.LOCATIONS_COLUMN_LONGITUDE));
 		float radius = cursor.getFloat(cursor.getColumnIndex(LocationSQLiteHelper.LOCATIONS_COLUMN_RADIUS));
 		long duration = cursor.getLong(cursor.getColumnIndex(LocationSQLiteHelper.LOCATIONS_COLUMN_DURATION));
 		int transition = cursor.getInt(cursor.getColumnIndex(LocationSQLiteHelper.LOCATIONS_COLUMN_TRANSITION_TYPE));
 		long item = cursor.getLong(cursor.getColumnIndex(LocationSQLiteHelper.LOCATIONS_COLUMN_ITEM_ID));
 		boolean completed = cursor.getInt(cursor.getColumnIndex(LocationSQLiteHelper.LOCATIONS_COLUMN_COMPLETED)) > 0;
 		
 		return new SimpleGeofence(id, latitude, longitude, radius, duration, transition, item, completed);
 	}
}
