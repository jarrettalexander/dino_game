package edu.uark.csce.mobile.dinogame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


// Manages Locations database for storing item location data
public class LocationSQLiteHelper extends SQLiteOpenHelper {

	// Database information
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "locations.db";
	public static final String LOCATIONS_TABLE_NAME = "locations"; // Stores location information
	
	// Locations table column names
	public static final String LOCATIONS_COLUMN_ID = "_id";
	public static final String LOCATIONS_COLUMN_LATITUDE = "latitude";
	public static final String LOCATIONS_COLUMN_LONGITUDE = "longitude";
	public static final String LOCATIONS_COLUMN_RADIUS = "radius";
	public static final String LOCATIONS_COLUMN_DURATION = "duration";
	public static final String LOCATIONS_COLUMN_TRANSITION_TYPE = "transition_type";
	public static final String LOCATIONS_COLUMN_ITEM_ID = "item_id";
	public static final String LOCATIONS_COLUMN_COMPLETED = "completed";
	
	// Locations table create statement
	private static final String LOCATIONS_TABLE_CREATE = 
			"CREATE TABLE " + LOCATIONS_TABLE_NAME + " (" + 
					LOCATIONS_COLUMN_ID + " INTEGER PRIMARY KEY, " + 
					LOCATIONS_COLUMN_LATITUDE + " DOUBLE NOT NULL, " + 
					LOCATIONS_COLUMN_LONGITUDE + " DOUBLE NOT NULL, " +
					LOCATIONS_COLUMN_RADIUS + " FLOAT NOT NULL, " +
					LOCATIONS_COLUMN_DURATION + " LONG, " +
					LOCATIONS_COLUMN_TRANSITION_TYPE + " INTEGER, " +
					LOCATIONS_COLUMN_ITEM_ID + " LONG, " +
					LOCATIONS_COLUMN_COMPLETED + " BOOLEAN " +
					");";
	
	public LocationSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(LOCATIONS_TABLE_CREATE);
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(LocationSQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data.");
		db.execSQL("DROP TABLE IF EXISTS " + LOCATIONS_TABLE_NAME);
		onCreate(db);
	}

}
