package edu.uark.csce.mobile.dinogame;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class InventoryActivity extends Activity {
	
	// Item Database
	private InventoryDataSource inventoryDatasource;
	
	// Geofence Database
	private SimpleGeofenceStore geofenceDatasource;
	
	// Holds position of the dino character that activated the InventoryActivity
	private int dinoPosition;
	
	// Used to send the position the user selected to the ItemActivity
	public final static String EXTRA_POSITION = "this.POSITION";
	
	// Holds whether or not the activity was started from a notification
	private boolean notificationMode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);
		
		// Get datasources
		inventoryDatasource = new InventoryDataSource(this);
		inventoryDatasource.open();
		
		geofenceDatasource = new SimpleGeofenceStore(this);
		geofenceDatasource.open();
		
		// Keep track of the currently selected dino character
		Intent intent = getIntent();
		dinoPosition = intent.getIntExtra(CharacterActivity.EXTRA_DINO_POSITION, -1);
		
		// On item click, pass index of currently selected dino and selected item to ItemActivity
		ListView listview = (ListView) findViewById(R.id.itemList);
		listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Intent intent = new Intent(InventoryActivity.this, ItemActivity.class);
				intent.putExtra(EXTRA_POSITION, position);
				intent.putExtra(CharacterActivity.EXTRA_DINO_POSITION, dinoPosition);
				startActivity(intent);
			}
		});

		// Store the data in the list view
		// testing from here...
//		int[] testArray = {1, 1, 1};
//		ByteBuffer byteBuffer = ByteBuffer.allocate(testArray.length * 4);        
//        IntBuffer intBuffer = byteBuffer.asIntBuffer();
//        intBuffer.put(testArray);
//        byte[] testByteArray = byteBuffer.array();
//        
//        Options options = new BitmapFactory.Options();
//        options.inScaled = false;
//        Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.hat_2_bmp, options);
//    	
//    	// Create a mutable copy of the bitmap
//		bp = bp.copy(Bitmap.Config.ARGB_8888, true);
//		ByteArrayOutputStream stream = new ByteArrayOutputStream();
//		bp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//		byte[] testimagebyteArray = stream.toByteArray();
//        
//		InventoryItem testItem = datasource.insertInventoryItem(new InventoryItem(12345, "hatter", testByteArray, testimagebyteArray, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF));
		// ... to here
		
		// Get the inventory items from the Datasource to display
		final List<InventoryItem> invItems = inventoryDatasource.getAllItems();
		final ArrayAdapter<InventoryItem> workoutAdapter = new ArrayAdapter<InventoryItem>(this,
				android.R.layout.simple_expandable_list_item_1, invItems);
		listview.setAdapter(workoutAdapter);
		
		Log.d(GeofenceUtils.APPTAG, "here are the items in inventory activity");
		for(int i = 0; i < invItems.size(); i++) {
			Log.d(GeofenceUtils.APPTAG, "item " + i + " = " + invItems.get(i).getId());
		}
		
		if (invItems.size() <= 0){
			Toast.makeText(this, "No items in inventory", Toast.LENGTH_LONG).show();
		} 
		
		// Retrieve intent from notification and show dialog
		Bundle extras = getIntent().getExtras();		
	    if(extras != null){
	    	
	    	notificationMode = extras.getBoolean("from_notification", false);
	    	
	    	// Get Geofence that sent the notification
	    	if(extras.getString("geofence_id") != null) {
		        Log.d(GeofenceUtils.APPTAG, "Extra:" + extras.getString("geofence_id"));
		        String geoId = extras.getString("geofence_id");
		        SimpleGeofence geo = geofenceDatasource.getGeofenceById(geoId);
		        
		        Log.d(GeofenceUtils.APPTAG, "Geo with id " + geo.getId() + " has item " + geo.getItemId());
		        
		        // Find item associated with activated Geofence
		        InventoryItem itemReceived = new InventoryItem();
		        for(InventoryItem item : invItems) {
		        	if(item.getId() == geo.getItemId()) {
		        		Log.d(GeofenceUtils.APPTAG, "found item in Inventory Activity");
		        		itemReceived = item;
		        		break;
		        	}
		        }
		        
		        // Show dialog informing user of newest item received
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
	}
	
	// Button listeners
	public void viewMap(View v) {
		Intent intent = new Intent(InventoryActivity.this, MapActivity.class);
		startActivity(intent);
	}

	public void cancel(View v) {
		Intent intent;
		if(notificationMode || dinoPosition == -1) {
			Log.d(GeofenceUtils.APPTAG, "in notification mode!");
			intent = new Intent(InventoryActivity.this, SummaryActivity.class);
		} else {
			intent = new Intent(InventoryActivity.this, CharacterActivity.class);
			intent.putExtra(EXTRA_POSITION, dinoPosition);
		}
		startActivity(intent);
	}

}
