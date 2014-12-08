package edu.uark.csce.mobile.dinogame;

import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class InventoryActivity extends Activity {
	
	// Item Database
	private InventoryDataSource datasource;
	
	// Geofence Database
	private SimpleGeofenceStore geofenceStore;
	
	public final static String EXTRA_POSITION = "this.POSITION";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);
		
		datasource = new InventoryDataSource(this);
		datasource.open();
		
		geofenceStore = new SimpleGeofenceStore(this);
		geofenceStore.open();
		
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
		
		Log.d(GeofenceUtils.APPTAG, "here are the items in inventory activity");
		for(int i = 0; i < invItems.size(); i++) {
			Log.d(GeofenceUtils.APPTAG, "item " + i + " = " + invItems.get(i).getId());
		}
		
		// Retrieve intent from notification and show dialog
		Bundle extras = getIntent().getExtras();
	    if(extras != null){
	        Log.d(GeofenceUtils.APPTAG, "Extra:" + extras.getString("geofence_id"));
	        String geoId = extras.getString("geofence_id");
	        SimpleGeofence geo = geofenceStore.getGeofenceById(geoId);
	        
	        InventoryItem itemReceived = new InventoryItem();
	        for(InventoryItem item : invItems) {
	        	if(item.getId() == geo.getItemId()) {
	        		Log.d(GeofenceUtils.APPTAG, "found item in Inventory Activity");
	        		itemReceived = item;
	        		break;
	        	}
	        }
	        
	        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
	        dialogBuilder.setTitle("Received Item!")
	        			.setMessage("Received Item: " + itemReceived.toString())
	        			.setPositiveButton("Sweet!", new DialogInterface.OnClickListener() {
			        				public void onClick(DialogInterface dialog, int id) {
			        					dialog.cancel();
			        				}
	        					});
	        
	        AlertDialog itemReceivedDialog = dialogBuilder.create();
	        itemReceivedDialog.show();
	        
	    }
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
