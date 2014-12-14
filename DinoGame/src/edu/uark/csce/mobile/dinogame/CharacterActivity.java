package edu.uark.csce.mobile.dinogame;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CharacterActivity extends Activity implements DeleteDinoDialogFragment.DeleteDinoDialogListener {
	
	// Database for dinos and items
	private DinosDataSource datasource;
	List<DinoItem> dinoItems;
	private InventoryDataSource inventoryDatasource;
	private InventoryItem equippedItem;	
	
	// Dino info
	private int position;
	private int invPosition;
	private DinoItem dino;
	private ArrayList<Integer> stats;
	private boolean equipped;
	private int[] color = {0, 0, 0};
	
	// Item info
	private String itemName;
	private ArrayList<Integer> itemStats;
	
	// Gallery pic info
	private String title;
	private String description;
	private String picUrl;
	
	// Info Views to display dino's data
	private TextView nameText;
	private TextView attackText;
	private TextView defenseText;
	private TextView specialText;
	private TextView equipText;
	private ProgressBar expBar;
	private ImageView dinoPic;
	
	// Image scale
	private static final int bitmapScale = 12; 
	private Bitmap unscaledDinoBitmap;
	
	// Used for extras to pass in dino's position to other activities
	public final static String EXTRA_DINO_POSITION = "this.DINO_POSITION";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_character);
		
		// Initialize the dino and item data
		datasource = new DinosDataSource(this);
		datasource.open();
		inventoryDatasource = new InventoryDataSource(this);
		inventoryDatasource.open();
		
		// Store the dinos in list
		dinoItems = datasource.getAllDinos();
		
		// Retrieve dino info
		Intent intent = getIntent();
		position = intent.getIntExtra(SummaryActivity.EXTRA_POSITION, 0);
		invPosition = intent.getIntExtra(InventoryActivity.EXTRA_POSITION, -1);
		if(invPosition != -1) {
			dino = dinoItems.get(invPosition);
		} else {			
			dino = dinoItems.get(position);
		}
		stats = new ArrayList<Integer>();
		itemStats = new ArrayList<Integer>();
		try {
			convertBytes(dino.getmStats(), stats);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d(GeofenceUtils.APPTAG, "dino equip = " + dino.getmEquip());
		if(dino.getmEquip() > 0) { 
			equipped = true;
			equippedItem = inventoryDatasource.getItemById(dino.getmEquip());
			itemName = equippedItem.getName();
			try {
				convertBytes(equippedItem.getStatEffects(), itemStats);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			equipped = false;
		}
		
		// Initialize pic information
		title = dino.getmName();
		description = "DinoGame character picture.";
		
		// Adjust Views based on data
		nameText = (TextView)findViewById(R.id.textViewNoItems);
		nameText.setText(dino.getmName());
		if(equipped) {
			if(equippedItem != null) {
				attackText = (TextView)findViewById(R.id.textView4);
				attackText.setText(Integer.toString(stats.get(0))+" + "+Integer.toString(itemStats.get(0)));
				defenseText = (TextView)findViewById(R.id.textView6);
				defenseText.setText(Integer.toString(stats.get(1))+" + "+Integer.toString(itemStats.get(1)));
				specialText = (TextView)findViewById(R.id.textView8);
				specialText.setText(Integer.toString(stats.get(2))+" + "+Integer.toString(itemStats.get(2)));
				equipText = (TextView)findViewById(R.id.textView11);
				equipText.setText(equippedItem.getName());
			}
		} else {
			attackText = (TextView)findViewById(R.id.textView4);
			attackText.setText(Integer.toString(stats.get(0)));
			defenseText = (TextView)findViewById(R.id.textView6);
			defenseText.setText(Integer.toString(stats.get(1)));
			specialText = (TextView)findViewById(R.id.textView8);
			specialText.setText(Integer.toString(stats.get(2)));
		}
		expBar = (ProgressBar)findViewById(R.id.expBar);
		expBar.setProgress(dino.getmExperience());
		
		// Draw dino and item images to activity
		drawDinoBitmap();
		drawItemBitmap();
		
	}
	
	/*---Button listeners---*/
	// Called when cancel is pressed
	public void viewSummary(View v) {
		Intent intent = new Intent(CharacterActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
	// Called when player enters battle with dino
	public void enterBattle(View v) {
		Intent intent = new Intent(CharacterActivity.this, BattleActivity.class);
		intent.putExtra(EXTRA_DINO_POSITION, position);
		startActivity(intent);
	}
	// Called when player views inventory activity to pick item for dino
	public void equipItem(View v) {
		Intent intent = new Intent(CharacterActivity.this, InventoryActivity.class);
		intent.putExtra(EXTRA_DINO_POSITION, position);
		startActivity(intent);
	}
	// Opens dialog box for player to verify they want to 'release' dino
	public void deleteDino(View v) {
		showDeleteDialog();
	}
	// Save picture of the dino to gallery
	public void saveDinoPic(View v) {
		dinoPic.setDrawingCacheEnabled(true);
	    Bitmap bp = dinoPic.getDrawingCache();
		ContentResolver c = getContentResolver();
		picUrl = insertImage(c, bp, title, description);
	}
	
	// Converts byte arrays for dino and item stats to array lists
	public void convertBytes(byte[] bytStats, ArrayList<Integer> Stats) throws IOException {

		if (bytStats != null) {
			ByteArrayInputStream bin = new ByteArrayInputStream(bytStats);
			DataInputStream din = new DataInputStream(bin);
			for (int i = 0; i < bytStats.length; i++) {
				Stats.add(Integer.valueOf(din.readInt()));
			}
		}

	}
	
	// Dialog functions
	public void showDeleteDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new DeleteDinoDialogFragment();
        dialog.show(getFragmentManager(), "NoticeDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the DeleteDinoDialogFragment.DeleteDinoDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
    	// Delete dino and return to Summary
    	Intent intent = new Intent(CharacterActivity.this, SummaryActivity.class);		
    	datasource.deleteDino(dino);
		startActivity(intent);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        // Nothing needs to be done
    }
    
    // Draws a dino character on screen from a bitmap
    private void drawDinoBitmap() {
    	
    	// Get color from dino object
//    	if(dino.getmColor() == 1)
//			color[0] = 204;
//		else if(dino.getmColor() == 2)
//			color[1] = 255;
//		else if(dino.getmColor() == 3)
//			color[2] = 255;
    	
    	// Handle resizing options to prevent blurring
    	Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.dinosaur, options);
    	
    	// Create a mutable copy of the bitmap
		bp = bp.copy(Bitmap.Config.ARGB_8888, true);
		bp.setHasAlpha(true);
		
		// Recolor dino based on greyscale image
	    for(int j = 0; j < bp.getHeight(); j++) {
	    	for(int i = 0; i < bp.getWidth(); i++) {
	    		if(bp.getPixel(i, j) == ColorUtils.COLOR_MAIN) {
	    			bp.setPixel(i, j, dino.getColorMain());
	    		} else if(bp.getPixel(i, j) == ColorUtils.COLOR_ACCENT_1) {
	    			bp.setPixel(i, j, dino.getColorAccent1());
	    		} else if(bp.getPixel(i, j) == ColorUtils.COLOR_ACCENT_2) {
	    			bp.setPixel(i, j, dino.getColorAccent2());
	    		} else if(bp.getPixel(i, j) == ColorUtils.COLOR_BACKGROUND) {
	    			bp.setPixel(i, j, Color.TRANSPARENT);
	    		}
	    	}
	    }
	    
	    unscaledDinoBitmap = bp;
	    
	    // Scale bitmap to appropriate size
	    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bp, bp.getWidth() * bitmapScale, bp.getHeight() * bitmapScale, false);
    		  
	    // Set ImageView to dino bitmap
	    dinoPic = (ImageView)findViewById(R.id.dinosaur);
	    dinoPic.setImageBitmap(scaledBitmap);
	    
	    // Unused alpha stuff
	    /*bp.setPixel(0, 0, Color.rgb(195, 195, 195));
	    int color1 = bp.getPixel(10, 10);
	    int color2 = Color.argb(255, 255, 255, 255);
	    int color3 = bp.getPixel(11, 10);
	    int alpha1 = Color.red(color1);
	    int alpha2 = Color.red(color2);
	    int alpha3 = Color.red(color3);
	    Log.e("alpha values", "alpha1 = " + alpha1 + ", alpha2 = " + alpha2 + ", alpha3 = " + alpha3);
	    if(color1 == color2) {
	    	Log.e("pixel test", "It's a match!!!"); }
	    if(color1 == color3) {
	    	Log.e("color test", "Matches itself!!!");
	    }*/
    }
    
    private void drawItemBitmap() {
    	// Handle resizing options to prevent blurring
    	Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.hat_top_hat, options);
    	
    	// Create a mutable copy of the bitmap
		bp = bp.copy(Bitmap.Config.ARGB_8888, true);
		bp.setHasAlpha(true);
		
		if(dino.getmEquip() > 0) {
			Log.d(GeofenceUtils.APPTAG, "this dino has something equipped");
			// Recolor item based on greyscale image
		    for(int j = 0; j < bp.getHeight(); j++) {
		    	for(int i = 0; i < bp.getWidth(); i++) {
		    		if(bp.getPixel(i, j) == ColorUtils.COLOR_MAIN) {
		    			//bp.setPixel(i, j, dino.getColorMain());
		    			bp.setPixel(i, j, Color.argb(255, 0, 17, 23));
		    		} else if(bp.getPixel(i, j) == ColorUtils.COLOR_ACCENT_1) {
		    			//bp.setPixel(i, j, dino.getColorAccent1());
		    			bp.setPixel(i, j, Color.argb(255, 0, 17, 23));
		    		} else if(bp.getPixel(i, j) == ColorUtils.COLOR_ACCENT_2) {
		    			//bp.setPixel(i, j, dino.getColorAccent2());
		    			bp.setPixel(i, j, Color.argb(255, 0, 17, 23));
		    		} else if(bp.getPixel(i, j) == ColorUtils.COLOR_BACKGROUND) {
		    			bp.setPixel(i, j, Color.TRANSPARENT);
		    		}
		    	}
		    }
		} else {
			Log.d(GeofenceUtils.APPTAG, "this dino has nothing equipped");
			for(int j = 0; j < bp.getHeight(); j++) {
				for(int i = 0; i < bp.getWidth(); i++) {
					bp.setPixel(i, j, Color.argb(0, 0, 0, 0));
				}
			}
		}
		
		// Add item bitmap to dino bitmap
		for(int j = 0; j < 11; j++) { // height
			for(int i = 11; i < unscaledDinoBitmap.getWidth(); i++) { // width from pixel 11 to 22
				if(bp.getPixel(i-11, j) != Color.TRANSPARENT)
					unscaledDinoBitmap.setPixel(i, j, bp.getPixel(i-11, j));
			}
		}
		
//		int[] pixels = {};
//		bp.getPixels(pixels, 0, bp.getWidth(), 0, 0, bp.getWidth(), bp.getHeight());
//		unscaledDinoBitmap.setPixels(pixels, 0, 12, 12, 0, bp.getWidth(), bp.getHeight());
	    
	    // Scale bitmap to appropriate size
	    Bitmap scaledDinoBitmap = Bitmap.createScaledBitmap(unscaledDinoBitmap, unscaledDinoBitmap.getWidth() * bitmapScale, unscaledDinoBitmap.getHeight() * bitmapScale, false);
    		  
	    // Set ImageView to item bitmap
	    dinoPic = (ImageView)findViewById(R.id.dinosaur);
	    dinoPic.setImageBitmap(scaledDinoBitmap);
    }
    
    // This function is used to save the character picture to the device gallery
    public static final String insertImage(ContentResolver cr, 
			Bitmap source, 
			String title, 
			String description) {
		
		ContentValues values = new ContentValues();
		values.put(Images.Media.TITLE, title);
		values.put(Images.Media.DISPLAY_NAME, title);
		values.put(Images.Media.DESCRIPTION, description);
		values.put(Images.Media.MIME_TYPE, "image/jpeg");
		// Add the date meta data to ensure the image is added at the front of the gallery
		values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
		values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
 
        Uri url = null;
        String stringUrl = null;    /* Value to be returned */
 
        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
 
            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                } finally {
                    imageOut.close();
                }
 
                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = Images.Thumbnails.getThumbnail(cr, id, Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                storeThumbnail(cr, miniThumb, id, 50F, 50F,Images.Thumbnails.MICRO_KIND);
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }
 
        if (url != null) {
            stringUrl = url.toString();
        }
 
        return stringUrl;
	}
    
    // Used with insertImage function to create the thumbnail metadata for inserting into gallery
    private static final Bitmap storeThumbnail(
    		ContentResolver cr,
    		Bitmap source,
    		long id,
    		float width, 
    		float height,
    		int kind) {

    	// Create the matrix to scale it
    	Matrix matrix = new Matrix();

    	float scaleX = width / source.getWidth();
    	float scaleY = height / source.getHeight();

    	matrix.setScale(scaleX, scaleY);

    	Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
    			source.getWidth(),
    			source.getHeight(), matrix,
    			true
    			);

    	ContentValues values = new ContentValues(4);
    	values.put(Images.Thumbnails.KIND,kind);
    	values.put(Images.Thumbnails.IMAGE_ID,(int)id);
    	values.put(Images.Thumbnails.HEIGHT,thumb.getHeight());
    	values.put(Images.Thumbnails.WIDTH,thumb.getWidth());

    	Uri url = cr.insert(Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

    	try {
    		OutputStream thumbOut = cr.openOutputStream(url);
    		thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
    		thumbOut.close();
    		return thumb;
    	} catch (FileNotFoundException ex) {
    		return null;
    	} catch (IOException ex) {
    		return null;
    	}
    }

}
