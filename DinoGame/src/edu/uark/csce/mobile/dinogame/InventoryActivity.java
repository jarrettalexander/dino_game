package edu.uark.csce.mobile.dinogame;

import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class InventoryActivity extends Activity {
	
	// Item Database
	private InventoryDataSource datasource;
	
	public final static String EXTRA_POSITION = "this.POSITION";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);
		
		datasource = new InventoryDataSource(this);
		datasource.open();
		
		ListView listview = (ListView) findViewById(R.id.itemList);
		listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Intent intent = new Intent(InventoryActivity.this, ItemActivity.class);
				intent.putExtra(EXTRA_POSITION, position);
				startActivity(intent);
			}
		});

		// Store the data in the list view
		final List<InventoryItem> invItems = datasource.getAllItems();
		final ArrayAdapter<InventoryItem> workoutAdapter = new ArrayAdapter<InventoryItem>(this,
				android.R.layout.simple_expandable_list_item_1, invItems);
		listview.setAdapter(workoutAdapter);
	}
	
	// Button listeners
	public void viewMap(View v) {
		Intent intent = new Intent(InventoryActivity.this, MapActivity.class);
		startActivity(intent);
	}

	public void cancel(View v) {
		Intent intent = new Intent(InventoryActivity.this, CharacterActivity.class);
		startActivity(intent);
	}

}
