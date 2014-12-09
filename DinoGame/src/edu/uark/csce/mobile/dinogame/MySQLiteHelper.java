package edu.uark.csce.mobile.dinogame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	// Character table
	public static final String TABLE_DINOS = "dinos";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_LEVEL = "level";
	public static final String COLUMN_EXP = "experience";
	public static final String COLUMN_STATS = "stats";
//	public static final String COLUMN_ATTACK = "attack";
//	public static final String COLUMN_DEFENSE = "defense";
//	public static final String COLUMN_SPECIAL = "special";
	public static final String COLUMN_COLOR_ONE = "color_one";
	public static final String COLUMN_COLOR_TWO = "color_two";
	public static final String COLUMN_COLOR_THREE = "color_three";
	public static final String COLUMN_EQUIP = "equipment";
	
	// Item table
	public static final String TABLE_ITEMS = "items";
	public static final String COLUMN_ITEM = "_id";
//	public static final String COLUMN_NAME = "name";	// just using one from dino table
	public static final String COLUMN_ICON = "icon";
	public static final String COLUMN_ITEM_COLOR_ONE = "color_one";
	public static final String COLUMN_ITEM_COLOR_TWO = "color_two";
	public static final String COLUMN_ITEM_COLOR_THREE = "color_three";
	public static final String COLUMN_STAT = "stats";

	private static final String DATABASE_NAME = "characters.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statements
	private static final String DATABASE_CREATE1 = "create table " + TABLE_DINOS + "(" + 
			COLUMN_ID + " integer primary key autoincrement, " + 
			COLUMN_NAME + " text not null, " + 
			COLUMN_DATE + " date not null, " + 
			COLUMN_LEVEL + " integer not null, " + 
			COLUMN_EXP + " integer not null, " + 
			COLUMN_STATS + " blob not null, " + 
			COLUMN_COLOR_ONE + " integer not null, " +
			COLUMN_COLOR_TWO + " integer not null, " +
			COLUMN_COLOR_THREE + " integer not null, " +
			COLUMN_EQUIP + " long, " + 
			"FOREIGN KEY ("+COLUMN_EQUIP+") REFERENCES "+TABLE_ITEMS+" ("+COLUMN_ITEM+"));";
	
	private static final String DATABASE_CREATE2 = "create table " + TABLE_ITEMS + "(" + 
			COLUMN_ITEM + " integer primary key, " + 
			COLUMN_NAME + " text not null, " + 
			COLUMN_ICON + " blob not null, " +
			COLUMN_ITEM_COLOR_ONE + " integer not null, " +
			COLUMN_ITEM_COLOR_TWO + " integer not null, " +
			COLUMN_ITEM_COLOR_THREE + " integer not null, " +
			COLUMN_STAT + " blob not null);";


	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE1);
		database.execSQL(DATABASE_CREATE2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DINOS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
		onCreate(db);
	}

}
