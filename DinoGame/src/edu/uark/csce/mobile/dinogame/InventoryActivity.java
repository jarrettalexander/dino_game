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
	private InventoryDataSource datasource;
	
	// Geofence Database
	private SimpleGeofenceStore geofenceStore;
	
	// Holds dino position
	private int dinoPosition;
	
	public final static String EXTRA_POSITION = "this.POSITION";
	
	private boolean notificationMode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);
		
		datasource = new InventoryDataSource(this);
		datasource.open();
		
		geofenceStore = new SimpleGeofenceStore(this);
		geofenceStore.open();
		
		Intent intent = getIntent();
		dinoPosition = intent.getIntExtra(CharacterActivity.EXTRA_DINO_POSITION, -1);
		
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
		
		final List<InventoryItem> invItems = datasource.getAllItems();
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
	    	
	    	if(extras.getString("geofence_id") != null) {
		        Log.d(GeofenceUtils.APPTAG, "Extra:" + extras.getString("geofence_id"));
		        String geoId = extras.getString("geofence_id");
		        SimpleGeofence geo = geofenceStore.getGeofenceById(geoId);
		        
		        Log.d(GeofenceUtils.APPTAG, "Geo with id " + geo.getId() + " has item " + geo.getItemId());
		        
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
		}
		startActivity(intent);
	}

}
