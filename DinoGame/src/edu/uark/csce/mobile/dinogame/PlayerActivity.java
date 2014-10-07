package edu.uark.csce.mobile.dinogame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PlayerActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
	}
	
	// Button listeners
	public void viewSummary(View v) {
		Intent intent = new Intent(PlayerActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
	
	public void viewAccount(View v) {
		Intent intent = new Intent(PlayerActivity.this, AccountActivity.class);
		startActivity(intent);
	}
	
	public void viewSettings(View v) {
		Intent intent = new Intent(PlayerActivity.this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void viewMap(View v) {
		Intent intent = new Intent(PlayerActivity.this, MapActivity.class);
		startActivity(intent);
	}

}
