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
			MySQLiteHelper.COLUMN_COLOR,
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

	public DinoItem createDinoItem(String name, Date date, int level, int experience, byte[] stats, int color, int equipID) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, name);
		values.put(MySQLiteHelper.COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd").format(date));
		values.put(MySQLiteHelper.COLUMN_LEVEL, level);
		values.put(MySQLiteHelper.COLUMN_EXP, experience);
		values.put(MySQLiteHelper.COLUMN_STATS, stats);
		values.put(MySQLiteHelper.COLUMN_COLOR, color);
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

	private DinoItem cursorToDino(Cursor cursor) throws ParseException {
		DinoItem dino = new DinoItem();
		dino.setmID(cursor.getLong(0));
		dino.setmName(cursor.getString(1));
		dino.setmCreatedDate(cursor.getString(2));
		dino.setmLevel(cursor.getInt(3));
		dino.setmExperience(cursor.getInt(4));
		dino.setmStats(cursor.getBlob(5));
		dino.setmColor(cursor.getInt(6));
		if(cursor.getInt(7) != -1) {
			dino.setmEquip(cursor.getInt(7));
		}
		return dino;
	}

}
