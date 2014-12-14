package edu.uark.csce.mobile.dinogame;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;

public class DinosDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_NAME,
			MySQLiteHelper.COLUMN_DATE,
			MySQLiteHelper.COLUMN_LEVEL,
			MySQLiteHelper.COLUMN_EXP,
			MySQLiteHelper.COLUMN_STATS,
			MySQLiteHelper.COLUMN_COLOR_ONE,
			MySQLiteHelper.COLUMN_COLOR_TWO,
			MySQLiteHelper.COLUMN_COLOR_THREE,
			MySQLiteHelper.COLUMN_EQUIP};
	private String listSort = MySQLiteHelper.COLUMN_DATE + " ASC";

	public DinosDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public DinoItem createDinoItem(String name, Date date, int level, int experience, byte[] stats, int colorMain, int colorAccent1, int colorAccent2, long equipID) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, name);
		values.put(MySQLiteHelper.COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd").format(date));
		values.put(MySQLiteHelper.COLUMN_LEVEL, level);
		values.put(MySQLiteHelper.COLUMN_EXP, experience);
		values.put(MySQLiteHelper.COLUMN_STATS, stats);
		values.put(MySQLiteHelper.COLUMN_COLOR_ONE, colorMain);
		values.put(MySQLiteHelper.COLUMN_COLOR_TWO, colorAccent1);
		values.put(MySQLiteHelper.COLUMN_COLOR_THREE, colorAccent2);
		if(equipID != -1) {
			values.put(MySQLiteHelper.COLUMN_EQUIP, equipID);
		}
		long insertId = database.insert(MySQLiteHelper.TABLE_DINOS, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_DINOS,
				allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		DinoItem newDino = cursorToDino(cursor);
		cursor.close();
		return newDino;
	}
	
	public void updateDinoEquip(DinoItem dino) {
		String strFilter = "_id=" + dino.getmID();
		ContentValues args = new ContentValues();
		args.put(MySQLiteHelper.COLUMN_EQUIP, dino.getmEquip());
		database.update(MySQLiteHelper.TABLE_DINOS, args, strFilter, null);
	}

	public void deleteDino(DinoItem dino) {
		long id = dino.getmID();
		System.out.println("Dino deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_DINOS, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<DinoItem> getAllDinos() {
		List<DinoItem> dinos = new ArrayList<DinoItem>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_DINOS,
				allColumns, null, null, null, null, listSort);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DinoItem dino = cursorToDino(cursor);
			dinos.add(dino);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return dinos;
	}
	
	/**
     * Sets the Completed flag to True for the Geofence in the local database with the associated ID.
     * 
     * @param id The ID of the geofence to set Completed to true
     */
    public void setDinoExp(String id, int level, int exp, byte[] stats) {
    	String where = "_id=" + id;
    	ContentValues args = new ContentValues();
    	args.put(MySQLiteHelper.COLUMN_LEVEL, level);
    	args.put(MySQLiteHelper.COLUMN_EXP, exp);
    	args.put(MySQLiteHelper.COLUMN_STATS, stats);
    	database.update(MySQLiteHelper.TABLE_DINOS, args, where, null);
    }

	private DinoItem cursorToDino(Cursor cursor) throws ParseException {
		DinoItem dino = new DinoItem();
		dino.setmID(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
		dino.setmName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NAME)));
		dino.setmCreatedDate(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_DATE)));
		dino.setmLevel(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_LEVEL)));
		dino.setmExperience(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EXP)));
		dino.setmStats(cursor.getBlob(cursor.getColumnIndex(MySQLiteHelper.COLUMN_STATS)));
		dino.setColorMain(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COLOR_ONE)));
		dino.setColorAccent1(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COLOR_TWO)));
		dino.setColorAccent2(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COLOR_THREE)));
		if(cursor.getInt(7) != -1) {
			dino.setmEquip(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EQUIP)));
		}
		return dino;
	}

}
