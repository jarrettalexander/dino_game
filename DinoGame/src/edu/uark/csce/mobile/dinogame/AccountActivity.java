package edu.uark.csce.mobile.dinogame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AccountActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
	}
	
	// Button listeners
	public void viewSummary(View v) {
		Intent intent = new Intent(AccountActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
	
	public void viewPlayer(View v) {
		Intent intent = new Intent(AccountActivity.this, CharacterActivity.class);
		startActivity(intent);
	}
	
	public void viewSettings(View v) {
		Intent intent = new Intent(AccountActivity.this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void viewMap(View v) {
		Intent intent = new Intent(AccountActivity.this, MapActivity.class);
		startActivity(intent);
	}

}
