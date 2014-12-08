package edu.uark.csce.mobile.dinogame;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ItemActivity extends Activity {
	
	// Database
	private InventoryDataSource datasource;
	List<InventoryItem> invItems;
	
	// Item info
	private int position;
	private InventoryItem item;
	private ArrayList<Integer> stats;
	private boolean equipped;
	
	// Info Views
	private TextView nameText;
	private TextView attackText;
	private TextView defenseText;
	private TextView specialText;
	private ImageView itemPic;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);
		
		datasource = new InventoryDataSource(this);
		datasource.open();
		
		// Store the items in list
		invItems = datasource.getAllItems();
		
		// Retrieve item info
		Intent intent = getIntent();
		position = intent.getIntExtra(InventoryActivity.EXTRA_POSITION, 0);
		item = invItems.get(position);
		stats = new ArrayList<Integer>();
		try {
			convertBytes(item.getStatEffects());
		} catch (IOException e) {
			e.printStackTrace();
		}
		bytesToBitmap(item.getIcon());
		
		// Set info in layout
		nameText = (TextView)findViewById(R.id.textView2);
		nameText.setText(item.getName() + ":");
		attackText = (TextView)findViewById(R.id.textView4);
		attackText.setText(Integer.toString(stats.get(0)) + "+");
		defenseText = (TextView)findViewById(R.id.textView6);
		defenseText.setText(Integer.toString(stats.get(1)) + "+");
		specialText = (TextView)findViewById(R.id.textView8);
		specialText.setText(Integer.toString(stats.get(2)) + "+");
	}
	
	// Button listeners
	public void equipItem(View v) {
		Intent intent = new Intent(ItemActivity.this, CharacterActivity.class);
		startActivity(intent);
	}
	
	public void cancelView(View v) {
		Intent intent = new Intent(ItemActivity.this, InventoryActivity.class);
		startActivity(intent);
	}
	
	// Converts byte array for stats to array list
	public void convertBytes(byte[] bytStats) throws IOException {

		if (bytStats != null) {
			ByteArrayInputStream bin = new ByteArrayInputStream(bytStats);
			DataInputStream din = new DataInputStream(bin);
			for (int i = 0; i < bytStats.length; i++) {
				stats.add(Integer.valueOf(din.readInt()));
			}
		}

	}
	
	// Converts byte array for icon to bitmap
	public void bytesToBitmap(byte[] data) {
		Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		itemPic = (ImageView) findViewById(R.id.imageView1);
		itemPic.setImageBitmap(bmp);
	}

}
