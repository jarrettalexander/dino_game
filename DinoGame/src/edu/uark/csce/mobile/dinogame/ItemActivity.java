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
		itemPic = (ImageView) findViewById(R.id.imageView1);
		// Not working currently!
		//bytesToBitmap(item.getIcon());
		
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
		// Handle resizing options to prevent blurring
    	Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		
		/*var documentsFolder = Environment.GetFolderPath (Environment.SpecialFolder.Personal);

	    //Create a folder for the images if not exists
	    System.IO.Directory.CreateDirectory(System.IO.Path.Combine (documentsFolder, "images"));

	    string imatge = System.IO.Path.Combine (documents, "images", "image.jpg");


	    System.out..WriteAllBytes(imatge, bytes.Concat(new Byte[]{(byte)0xD9}).ToArray());

	    bitmap = BitmapFactory.DecodeFile(imatge);*/
		
        Log.e("debugging", "data: " + data);
        Log.e("debugging", "bitmap: " + bmp);
        
        // Create a mutable copy of the bitmap
        bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmp.setHasAlpha(true);

        // Recolor item based on greyscale image
        for(int j = 0; j < bmp.getHeight(); j++) {
        	for(int i = 0; i < bmp.getWidth(); i++) {
        		if(bmp.getPixel(i, j) == ColorUtils.COLOR_MAIN) {
        			//bmp.setPixel(i, j, Color.argb(255, color[0], color[1], color[2]));
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
        
		itemPic.setImageBitmap(bmp);
	}

}
