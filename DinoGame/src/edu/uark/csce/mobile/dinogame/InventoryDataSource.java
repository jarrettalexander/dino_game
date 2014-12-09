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
			MySQLiteHelper.COLUMN_ICON,
			MySQLiteHelper.COLUMN_ITEM_COLOR_ONE,
			MySQLiteHelper.COLUMN_ITEM_COLOR_TWO,
			MySQLiteHelper.COLUMN_ITEM_COLOR_THREE};
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
	public InventoryItem insertInventoryItem(InventoryItem item){
		return this.createInventoryItem(item.getId(), item.getName(), item.getStatEffects(), item.getIcon(), item.getColorMain(), item.getColorAccent1(), item.getColorAccent2());
	}
	public InventoryItem createInventoryItem(long id, String name, byte[] stats, byte[] icon, int colorMain, int colorAccent1, int colorAccent2) {
		
		Cursor alreadyPresent = database.query(MySQLiteHelper.TABLE_ITEMS, allColumns, MySQLiteHelper.COLUMN_ITEM + " = " + id, null, null, null, null);
    	if(alreadyPresent.getCount() <= 0) {
		
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.COLUMN_ITEM, id);
			values.put(MySQLiteHelper.COLUMN_NAME, name);
			values.put(MySQLiteHelper.COLUMN_STAT, stats);
			values.put(MySQLiteHelper.COLUMN_ICON, icon);
			values.put(MySQLiteHelper.COLUMN_ITEM_COLOR_ONE, colorMain);
			values.put(MySQLiteHelper.COLUMN_ITEM_COLOR_TWO, colorAccent1);
			values.put(MySQLiteHelper.COLUMN_ITEM_COLOR_THREE, colorAccent2);
			long insertId = database.insert(MySQLiteHelper.TABLE_ITEMS, null,
					values);
			Cursor cursor = database.query(MySQLiteHelper.TABLE_ITEMS,
					allColumns, MySQLiteHelper.COLUMN_ITEM + " = " + id, null,
					null, null, null);
			cursor.moveToFirst();
			InventoryItem newItem = cursorToItem(cursor);
			cursor.close();
			return newItem;
    	} else {
    		return null;
    	}
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
	
	public InventoryItem getItemById(long id) {
		String query = "SELECT * FROM " + MySQLiteHelper.TABLE_ITEMS + " WHERE " + MySQLiteHelper.COLUMN_ITEM + " = " + id;
		Cursor cursor = database.rawQuery(query, null);
		cursor.moveToFirst();
		if(cursor.getCount() > 0) {
			InventoryItem item = cursorToItem(cursor);
			return item;
		} else {
			return null;
		}
	}

	private InventoryItem cursorToItem(Cursor cursor) throws ParseException {
		InventoryItem invItem = new InventoryItem();
		invItem.setId(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITEM)));
		invItem.setName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NAME)));
		invItem.setStatEffects(cursor.getBlob(cursor.getColumnIndex(MySQLiteHelper.COLUMN_STAT)));
		invItem.setIcon(cursor.getBlob(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ICON)));
		invItem.setColorMain(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITEM_COLOR_ONE)));
		invItem.setColorAccent1(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITEM_COLOR_TWO)));
		invItem.setColorAccent2(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ITEM_COLOR_THREE)));
		return invItem;
	}

}
