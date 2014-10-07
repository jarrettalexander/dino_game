package edu.uark.csce.mobile.dinogame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MapActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
	}
	
	// Button listeners
	public void viewSummary(View v) {
		Intent intent = new Intent(MapActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
	
	public void viewPlayer(View v) {
		Intent intent = new Intent(MapActivity.this, PlayerActivity.class);
		startActivity(intent);
	}
	
	public void viewAccount(View v) {
		Intent intent = new Intent(MapActivity.this, AccountActivity.class);
		startActivity(intent);
	}
	
	public void viewSettings(View v) {
		Intent intent = new Intent(MapActivity.this, SettingsActivity.class);
		startActivity(intent);
	}

}
