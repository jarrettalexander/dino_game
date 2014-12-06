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
			MySQLiteHelper.COLUMN_ATTACK,
			MySQLiteHelper.COLUMN_DEFENSE,
			MySQLiteHelper.COLUMN_SPECIAL,
			MySQLiteHelper.COLUMN_IMG};
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

	public DinoItem createDinoItem(String name, Date date, int level, int experience, int attack, int defense, int special, byte[] image) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, name);
		values.put(MySQLiteHelper.COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd").format(date));
		values.put(MySQLiteHelper.COLUMN_LEVEL, level);
		values.put(MySQLiteHelper.COLUMN_EXP, experience);
		values.put(MySQLiteHelper.COLUMN_ATTACK, attack);
		values.put(MySQLiteHelper.COLUMN_DEFENSE, defense);
		values.put(MySQLiteHelper.COLUMN_SPECIAL, special);
		values.put(MySQLiteHelper.COLUMN_IMG, image);
		long insertId = database.insert(MySQLiteHelper.TABLE_DINOS, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_DINOS,
				allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		DinoItem newDino = cursorToWorkout(cursor);
		cursor.close();
		return newDino;
	}

	public void deleteDino(DinoItem dino) {
		long id = dino.getmID();
		System.out.println("Workout deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_DINOS, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<DinoItem> getAllDinos() {
		List<DinoItem> dinos = new ArrayList<DinoItem>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_DINOS,
				allColumns, null, null, null, null, listSort);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DinoItem dino = cursorToWorkout(cursor);
			dinos.add(dino);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return dinos;
	}

	private DinoItem cursorToWorkout(Cursor cursor) throws ParseException {
		DinoItem dino = new DinoItem();
		dino.setmID(cursor.getLong(0));
		dino.setmName(cursor.getString(1));
//		Date d = new Date();
//		try {
//	         d =  new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(2));
//	         dino.setmCreatedDate(d);
//	    } catch (ParseException e) {
//	        e.printStackTrace();
//	    } finally {
//	    	
//	    }
		dino.setmCreatedDate(cursor.getString(2));
		dino.setmLevel(cursor.getInt(3));
		dino.setmExperience(cursor.getInt(4));
		dino.setmAttack(cursor.getInt(5));
		dino.setmDefense(cursor.getInt(6));
		dino.setmSpecial(cursor.getInt(7));
		dino.setmImage(cursor.getBlob(8));
		return dino;
	}
	
	// For converting from ArrayList<Double> to byte array
	public static byte[] convertList(ArrayList<Double> list) throws IOException {

	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    DataOutputStream dout = new DataOutputStream(bout);
	    for (double d : list) {
	        dout.writeDouble(d);
	    }
	    dout.close();
	    return bout.toByteArray();
	}

}
