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
		nameText = (TextView)findViewById(R.id.textView2);
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
		if(dino.getmColor() == 1)
			color[0] = 204;
		else if(dino.getmColor() == 2)
			color[1] = 255;
		else if(dino.getmColor() == 3)
			color[2] = 255;
		
		// Adjust dino color
		dinoPic = (ImageView)findViewById(R.id.dinosaur);
		Drawable d = getResources().getDrawable(R.drawable.dinosaur_bigger);
	    Bitmap bp = ((BitmapDrawable)d).getBitmap();
	    int color2 = Color.argb(255, 255, 255, 255);
	    /*for(int i = 1; i < bp.getHeight(); i++) {
	    	for(int j = 1; j < bp.getWidth(); j++) {
	    		if(bp.getPixel(i, j) == color2)
	    			bp.setPixel(i, j, Color.argb(255, color[0], color[1], color[2]));
	    	}
	    }*/
	    
//	    bp.setPixel(10, 10, Color.argb(255, color[0], color[1], color[2]));
//	    bp.setPixel(11, 10, Color.argb(255, color[0], color[1], color[2]));
//	    
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
	
	// Button listeners
	public void viewSummary(View v) {
		Intent intent = new Intent(CharacterActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
	
	public void viewAccount(View v) {
		Intent intent = new Intent(CharacterActivity.this, AccountActivity.class);
		startActivity(intent);
	}
	
	public void viewSettings(View v) {
		Intent intent = new Intent(CharacterActivity.this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void viewMap(View v) {
		Intent intent = new Intent(CharacterActivity.this, MapActivity.class);
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

}
