package edu.uark.csce.mobile.dinogame;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CharacterActivity extends Activity {
	
	// Database
	private DinosDataSource datasource;
	List<DinoItem> dinoItems;
	
	// Dino info
	private int position;
	private DinoItem dino;
	private ArrayList<Integer> stats;
	private boolean equipped;
	
	// Textviews
	private TextView nameText;
	private TextView attackText;
	private TextView defenseText;
	private TextView specialText;
	private TextView equipText;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_character);
		
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
		
		// Adjust TextViews based on data
		nameText = (TextView)findViewById(R.id.textView2);
		nameText.setText(dino.getmName());
		attackText = (TextView)findViewById(R.id.textView4);
		attackText.setText(Integer.toString(stats.get(0)));
		defenseText = (TextView)findViewById(R.id.textView6);
		defenseText.setText(Integer.toString(stats.get(1)));
		specialText = (TextView)findViewById(R.id.textView8);
		specialText.setText(Integer.toString(stats.get(2)));
		if(equipped) {
			equipText = (TextView)findViewById(R.id.textView11);
			equipText.setText("a hat");
			// TODO Retrieve item information and display it
		}
	}
	
	// Button listeners
	public void viewSummary(View v) {
		Intent intent = new Intent(CharacterActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
	
	public void viewAccount(View v) {
		Intent intent = new Intent(CharacterActivity.this, AccountActivity.class);
		startActivity(intent);
	}
	
	public void viewSettings(View v) {
		Intent intent = new Intent(CharacterActivity.this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void viewMap(View v) {
		Intent intent = new Intent(CharacterActivity.this, MapActivity.class);
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
