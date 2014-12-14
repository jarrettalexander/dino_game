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
import android.graphics.Color;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ItemActivity extends Activity {
	
	// Database
	private InventoryDataSource inventoryDatasource;
	List<InventoryItem> invItems;
	
	private DinosDataSource dinoDatasource;
	List<DinoItem> dinoItems;
	
	// Item info
	private int itemPosition;
	private int dinoPosition;
	
	private InventoryItem item;
	private ArrayList<Integer> stats;
	private boolean equipped;
	
	private DinoItem currentDino;
	
	// Info Views
	private TextView nameText;
	private TextView attackText;
	private TextView defenseText;
	private TextView specialText;
	private ImageView itemPic;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);
		
		// Open datasources
		inventoryDatasource = new InventoryDataSource(this);
		inventoryDatasource.open();
		
		dinoDatasource = new DinosDataSource(this);
		dinoDatasource.open();
		
		// Store the items from local database into list
		invItems = inventoryDatasource.getAllItems();
		
		// Store the dinos from local database into list
		dinoItems = dinoDatasource.getAllDinos();
		
		// Get the currently activated the dino character
		Intent intent = getIntent();
		dinoPosition = intent.getIntExtra(CharacterActivity.EXTRA_DINO_POSITION, -1);
		if(dinoPosition >= 0) {
			currentDino = dinoItems.get(dinoPosition);
		} else {
			Button equipButton = (Button)findViewById(R.id.equipButton);
			equipButton.setEnabled(false);
		}
		
		// Get the item the user selected from InventoryActivity
		itemPosition = intent.getIntExtra(InventoryActivity.EXTRA_POSITION, 0);
		item = invItems.get(itemPosition);
		
		stats = new ArrayList<Integer>();
		try {
			convertBytes(item.getStatEffects());
		} catch (IOException e) {
			e.printStackTrace();
		}
		itemPic = (ImageView) findViewById(R.id.imageView1);
		
		//TODO: Bitmap decoding is currently not working correctly; Use placeholder instead for now
		//bytesToBitmap(item.getIcon());
		drawPlaceholderItemBitmap();
		
		// Set info in layout
		nameText = (TextView)findViewById(R.id.itemName);
		nameText.setText(item.getName() + ":");
		attackText = (TextView)findViewById(R.id.attackText);
		attackText.setText("+" + Integer.toString(stats.get(0)));
		defenseText = (TextView)findViewById(R.id.defenseText);
		defenseText.setText("+" + Integer.toString(stats.get(1)));
		specialText = (TextView)findViewById(R.id.specialText);
		specialText.setText("+" + Integer.toString(stats.get(2)));
	}
	
	// Button listeners
	public void equipItem(View v) {
		
		// Update equipment for current dino
		currentDino.setmEquip(item.getId().intValue());
		dinoDatasource.updateDinoEquip(currentDino);
		
		// Return to current dino's character screen
		Intent intent = new Intent(ItemActivity.this, CharacterActivity.class);
		intent.putExtra(SummaryActivity.EXTRA_POSITION, dinoPosition);
		startActivity(intent);
	}
	
	public void cancelView(View v) {
		Intent intent = new Intent(ItemActivity.this, InventoryActivity.class);
		intent.putExtra(CharacterActivity.EXTRA_DINO_POSITION, dinoPosition);
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
	
	// Converts byte array for icon to bitmap; Currently not working
	public void bytesToBitmap(byte[] data) {
		// Handle resizing options to prevent blurring
    	Options options = new BitmapFactory.Options();
        options.inScaled = false;
        
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		
        Log.e("debugging", "data: " + data);
        Log.e("debugging", "bitmap: " + bmp);
        
        // Create a mutable copy of the bitmap
        bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmp.setHasAlpha(true);

        // Recolor item based on pre-processed image
        for(int j = 0; j < bmp.getHeight(); j++) {
        	for(int i = 0; i < bmp.getWidth(); i++) {
        		if(bmp.getPixel(i, j) == ColorUtils.COLOR_MAIN) {
        			bmp.setPixel(i, j, item.getColorMain());
        		} else if(bmp.getPixel(i, j) == ColorUtils.COLOR_ACCENT_1) {
        			bmp.setPixel(i, j, item.getColorAccent1());
        		} else if(bmp.getPixel(i, j) == ColorUtils.COLOR_ACCENT_2) {
        			bmp.setPixel(i, j, item.getColorAccent2());
        		} else if(bmp.getPixel(i, j) == ColorUtils.COLOR_BACKGROUND) {
        			bmp.setPixel(i, j, Color.TRANSPARENT);
        		}
        	}
        }
        
        // Scale bitmap to appropriate size
        bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth() * 12, bmp.getHeight() * 12, false);
        
        // Set ImageView to item bitmap
		itemPic.setImageBitmap(bmp);
	}
	
	// Used to draw a placeholder item bitmap while bytesToBitmap method is broken
	private void drawPlaceholderItemBitmap() {
		
		// Handle resizing options to prevent blurring
    	Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Use pre-packaged item bitmap
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.hat_top_hat, options);
        
        // Create a mutable copy of the bitmap
        bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmp.setHasAlpha(true);

        // Recolor item based on pre-processed image
        for(int j = 0; j < bmp.getHeight(); j++) {
        	for(int i = 0; i < bmp.getWidth(); i++) {
//        		if(bmp.getPixel(i, j) == ColorUtils.COLOR_MAIN) {
//        			bmp.setPixel(i, j, item.getColorMain());
//        		} else if(bmp.getPixel(i, j) == ColorUtils.COLOR_ACCENT_1) {
//        			bmp.setPixel(i, j, item.getColorAccent1());
//        		} else if(bmp.getPixel(i, j) == ColorUtils.COLOR_ACCENT_2) {
//        			bmp.setPixel(i, j, item.getColorAccent2());
//        		} else if(bmp.getPixel(i, j) == ColorUtils.COLOR_BACKGROUND) {
//        			bmp.setPixel(i, j, Color.TRANSPARENT);
//        		}
        		if(bmp.getPixel(i, j) == ColorUtils.COLOR_BACKGROUND) {
        			bmp.setPixel(i, j, Color.TRANSPARENT);
        		}
        	 }
        }
        
        // Scale bitmap to appropriate size
        bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth() * 12, bmp.getHeight() * 12, false);
        
        // Set ImageView to item bitmap
		itemPic.setImageBitmap(bmp);
	}

}
