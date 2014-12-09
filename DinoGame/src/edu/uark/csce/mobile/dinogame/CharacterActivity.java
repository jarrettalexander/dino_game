package edu.uark.csce.mobile.dinogame;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CharacterActivity extends Activity implements DeleteDinoDialogFragment.DeleteDinoDialogListener {
	
	// Database
	private DinosDataSource datasource;
	List<DinoItem> dinoItems;
	
	// Dino info
	private int position;
	private DinoItem dino;
	private ArrayList<Integer> stats;
	private boolean equipped;
	private int[] color = {0, 0, 0};
	
	// Info Views
	private TextView nameText;
	private TextView attackText;
	private TextView defenseText;
	private TextView specialText;
	private TextView equipText;
	private ProgressBar expBar;
	private ImageView dinoPic;
	
	public final static String EXTRA_POSITION = "this.POSITION";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_character);
		
		datasource = new DinosDataSource(this);
		datasource.open();
		
		// Store the dinos in list
		dinoItems = datasource.getAllDinos();
		
		// Retrieve dino info
		Intent intent = getIntent();
		position = intent.getIntExtra(SummaryActivity.EXTRA_POSITION, 0);
		dino = dinoItems.get(position);
		stats = new ArrayList<Integer>();
		try {
			convertBytes(dino.getmStats());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(dino.getmEquip() == -1)
			equipped = false;
		
		// Adjust Views based on data
		nameText = (TextView)findViewById(R.id.textViewNoItems);
		nameText.setText(dino.getmName());
		attackText = (TextView)findViewById(R.id.textView4);
		attackText.setText(Integer.toString(stats.get(0)));
		defenseText = (TextView)findViewById(R.id.textView6);
		defenseText.setText(Integer.toString(stats.get(1)));
		specialText = (TextView)findViewById(R.id.textView8);
		specialText.setText(Integer.toString(stats.get(2)));
		if(equipped) {
			equipText = (TextView)findViewById(R.id.textView11);
			equipText.setText("a hat");
			// TODO Retrieve item information and display it
		}
		expBar = (ProgressBar)findViewById(R.id.expBar);
		expBar.setProgress(dino.getmExperience());
		
		drawDinoBitmap();
		
	}
	
	// Button listeners
	public void viewSummary(View v) {
		Intent intent = new Intent(CharacterActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
	
	public void enterBattle(View v) {
		Intent intent = new Intent(CharacterActivity.this, BattleActivity.class);
		intent.putExtra(EXTRA_POSITION, position);
		startActivity(intent);
	}
	
	public void equipItem(View v) {
		Intent intent = new Intent(CharacterActivity.this, InventoryActivity.class);
		startActivity(intent);
	}
	
	public void deleteDino(View v) {
		showDeleteDialog();
	}
	
	// Converts byte arrays for latitudes and longitudes to array lists
	public void convertBytes(byte[] bytStats) throws IOException {

		if (bytStats != null) {
			ByteArrayInputStream bin = new ByteArrayInputStream(bytStats);
			DataInputStream din = new DataInputStream(bin);
			for (int i = 0; i < bytStats.length; i++) {
				stats.add(Integer.valueOf(din.readInt()));
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
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
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
	    			//bp.setPixel(i, j, Color.argb(255, color[0], color[1], color[2]));
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
	    
	    // Scale bitmap to appropriate size
	    bp = Bitmap.createScaledBitmap(bp, bp.getWidth() * 12, bp.getHeight() * 12, false);
    		  
	    // Set ImageView to dino bitmap
	    dinoPic = (ImageView)findViewById(R.id.dinosaur);
	    dinoPic.setImageBitmap(bp);
	    
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

}
