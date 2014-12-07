package edu.uark.csce.mobile.dinogame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}
	
	// Button listeners
	public void viewSummary(View v) {
		Intent intent = new Intent(SettingsActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
	
	public void viewPlayer(View v) {
		Intent intent = new Intent(SettingsActivity.this, CharacterActivity.class);
		startActivity(intent);
	}
	
	public void viewAccount(View v) {
		Intent intent = new Intent(SettingsActivity.this, AccountActivity.class);
		startActivity(intent);
	}
	
	public void viewMap(View v) {
		Intent intent = new Intent(SettingsActivity.this, MapActivity.class);
		startActivity(intent);
	}

}
