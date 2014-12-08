package edu.uark.csce.mobile.dinogame;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ItemActivity extends Activity {
	
	// Database
	private InventoryDataSource datasource;
	List<InventoryItem> invItems;
	
	// Dino info
	private int position;
	private InventoryItem item;
	private ArrayList<Integer> stats;
	private boolean equipped;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_character);
		
		datasource = new InventoryDataSource(this);
		datasource.open();
		
		// Store the items in list
		invItems = datasource.getAllItems();
		
		// Retrieve item info
		Intent intent = getIntent();
		position = intent.getIntExtra(InventoryActivity.EXTRA_POSITION, 0);
		item = invItems.get(position);
		stats = new ArrayList<Integer>();
		try {
			convertBytes(item.getStatEffects());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Converts byte arrays for latitudes and longitudes to array lists
	public void convertBytes(byte[] bytStats) throws IOException {

		if (bytStats != null) {
			ByteArrayInputStream bin = new ByteArrayInputStream(bytStats);
			DataInputStream din = new DataInputStream(bin);
			for (int i = 0; i < bytStats.length; i++) {
				stats.add(Integer.valueOf(din.readInt()));
			}
		}

	}

}
