package edu.uark.csce.mobile.dinogame;

import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SummaryActivity extends ActionBarActivity {
	
	// Database
	private DinosDataSource datasource;
	
	public final static String EXTRA_POSITION = "this.POSITION";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);
		
		datasource = new DinosDataSource(this);
		datasource.open();
		
		ListView listview = (ListView) findViewById(R.id.listView1);
		listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Intent intent = new Intent(SummaryActivity.this, PlayerActivity.class);
				intent.putExtra(EXTRA_POSITION, position);
				startActivity(intent);
			}
		});

		// Store the data in the list view
		final List<DinoItem> dinoItems = datasource.getAllDinos();
		final ArrayAdapter<DinoItem> workoutAdapter = new ArrayAdapter<DinoItem>(this,
				android.R.layout.simple_expandable_list_item_1, dinoItems);
		listview.setAdapter(workoutAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.summary, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// Button listeners
	public void viewPlayer(View v) {
		Intent intent = new Intent(SummaryActivity.this, PlayerActivity.class);
		startActivity(intent);
	}
	
	public void viewAccount(View v) {
		Intent intent = new Intent(SummaryActivity.this, AccountActivity.class);
		startActivity(intent);
	}
	
	public void viewSettings(View v) {
		Intent intent = new Intent(SummaryActivity.this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void viewMap(View v) {
		Intent intent = new Intent(SummaryActivity.this, MapActivity.class);
		startActivity(intent);
	}
	
	public void createDino(View v) {
		Intent intent = new Intent(SummaryActivity.this, CreateDinoActivity.class);
		startActivity(intent);
	}
}
