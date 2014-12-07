package edu.uark.csce.mobile.dinogame;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BattleActivity extends Activity {
	
	// Database
	private DinosDataSource datasource;
	List<DinoItem> dinoItems;
	
	// Dino info
	private int position;
	private DinoItem dino;
	private ArrayList<Integer> stats;
	private boolean equipped;
	private int[] color = {0, 0, 0};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);
		
		datasource = new DinosDataSource(this);
		datasource.open();
		
		// Store the dinos in list
		dinoItems = datasource.getAllDinos();

		// Retrieve dino info
		Intent intent = getIntent();
		position = intent.getIntExtra(SummaryActivity.EXTRA_POSITION, 0);
		dino = dinoItems.get(position);
		stats = new ArrayList<Integer>();
		try {
			convertBytes(dino.getmStats());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(dino.getmEquip() == -1)
			equipped = false;
		
	}
	
	// Button listeners
	public void forfeit(View v) {
		Intent intent = new Intent(BattleActivity.this, SummaryActivity.class);
		startActivity(intent);
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
