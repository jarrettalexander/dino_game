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

public class InventoryDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ITEM,
			MySQLiteHelper.COLUMN_NAME,
			MySQLiteHelper.COLUMN_STAT,
			MySQLiteHelper.COLUMN_ICON};
	private String listSort = MySQLiteHelper.COLUMN_NAME;

	public InventoryDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public InventoryItem createInventoryItem(String name, byte[] stats, byte[] icon) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, name);
		values.put(MySQLiteHelper.COLUMN_STAT, stats);
		values.put(MySQLiteHelper.COLUMN_ICON, icon);
		long insertId = database.insert(MySQLiteHelper.TABLE_ITEMS, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEMS,
				allColumns, MySQLiteHelper.COLUMN_ITEM + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		InventoryItem newItem = cursorToItem(cursor);
		cursor.close();
		return newItem;
	}

	public void deleteDino(InventoryItem invItem) {
		long id = invItem.getId();
		System.out.println("Dino deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_ITEMS, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<InventoryItem> getAllItems() {
		List<InventoryItem> invItems = new ArrayList<InventoryItem>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEMS,
				allColumns, null, null, null, null, listSort);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			InventoryItem invItem = cursorToItem(cursor);
			invItems.add(invItem);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return invItems;
	}

	private InventoryItem cursorToItem(Cursor cursor) throws ParseException {
		InventoryItem invItem = new InventoryItem();
		invItem.setId(cursor.getLong(0));
		invItem.setName(cursor.getString(1));
		invItem.setStatEffects(cursor.getBlob(2));
		invItem.setIcon(cursor.getBlob(3));
		return invItem;
	}

}
