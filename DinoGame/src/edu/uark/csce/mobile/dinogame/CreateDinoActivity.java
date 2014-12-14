package edu.uark.csce.mobile.dinogame;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import yuku.ambilwarna.AmbilWarnaDialog;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class CreateDinoActivity extends Activity {
	
	// Edit fields
	private EditText nameText;
	private Button buttonChooseColor1;
	private Button buttonChooseColor2;
	private Button buttonChooseColor3;
	
	// Database for dinos
	private DinosDataSource datasource;
	private List<DinoItem> dinoItems;
	
	// Created dino data
	private String name;
	private String colorText;
	private int[] stats = {1, 1, 1};
	private byte[] bytStats;
	private int colorMain;
	private int colorAccent1;
	private int colorAccent2;
	
	// Random stat generator
	private Random statGen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		
		// Open dino database so we can insert
		datasource = new DinosDataSource(this);
		datasource.open();
		
		// Store the dinos in list
		dinoItems = datasource.getAllDinos();
		
		// Initialize created dino data
		name = null;
		bytStats = null;
		colorMain = 0xFFFF0000;
		colorAccent1 = 0xFF00FF00;
		colorAccent2 = 0xFF0000FF;
		
		// Initialize Random generator
		statGen = new Random();
		
		// Register text fields
		nameText = (EditText)findViewById(R.id.nameField);
		
		/*---Setup color pickers---*/
		// The following code allows the player to select character colors from
		// color wheels.
		buttonChooseColor1 = (Button)findViewById(R.id.buttonChooseColor1);
		buttonChooseColor1.setBackgroundColor(colorMain);
		buttonChooseColor1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openColorDialog1(v);
			}
		});
		
		buttonChooseColor2 = (Button)findViewById(R.id.buttonChooseColor2);
		buttonChooseColor2.setBackgroundColor(colorAccent1);
		buttonChooseColor2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openColorDialog2(v);
			}
		});
		
		buttonChooseColor3 = (Button)findViewById(R.id.buttonChooseColor3);
		buttonChooseColor3.setBackgroundColor(colorAccent2);
		buttonChooseColor3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openColorDialog3(v);
			}
		});
		
	}
	
	// Button listeners
	// Open color picker dialog
	public void openColorDialog1(View v) {
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(CreateDinoActivity.this, 0xffffff00, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
			@Override
			public void onOk(AmbilWarnaDialog dialog, int col) {
				//Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
				// Set dino color element
				colorMain = col;
				buttonChooseColor1.setBackgroundColor(col);
			}

			@Override
			public void onCancel(AmbilWarnaDialog dialog) {
				//Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
			}
		});
		dialog.show();
	}
	// Open color picker dialog
	public void openColorDialog2(View v) {
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(CreateDinoActivity.this, 0xffffff00, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
			@Override
			public void onOk(AmbilWarnaDialog dialog, int col) {
				//Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
				// Set dino color element
				colorAccent1 = col;
				buttonChooseColor2.setBackgroundColor(col);
			}

			@Override
			public void onCancel(AmbilWarnaDialog dialog) {
				//Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
			}
		});
		dialog.show();
	}
	// Open color picker dialog
	public void openColorDialog3(View v) {
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(CreateDinoActivity.this, 0xffffff00, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
			@Override
			public void onOk(AmbilWarnaDialog dialog, int col) {
				//Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
				// Set dino color element
				colorAccent2 = col;
				buttonChooseColor3.setBackgroundColor(col);
			}

			@Override
			public void onCancel(AmbilWarnaDialog dialog) {
				//Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
			}
		});
		dialog.show();
	}
	// Save/Create dino to database
	public void saveDino(View v) throws IOException {
		Intent intent = new Intent(CreateDinoActivity.this, SummaryActivity.class);
		// Set character values
		try{
			name = nameText.getText().toString();
		}catch(Exception e){
			e.printStackTrace();
			name="didntwork";
		}
		
		//Log.d(GeofenceUtils.APPTAG, "colors are: " + Integer.toHexString(colorMain) + ", " + Integer.toHexString(colorAccent1) + ", " + Integer.toHexString(colorAccent2));
		// Randomly initialize base stats to either 1 or 2 for each
		stats[0] = (statGen.nextInt(2) + 1);
		stats[1] = (statGen.nextInt(2) + 1);
		stats[2] = (statGen.nextInt(2) + 1);
		// Convert int array of stats to byte array
		bytStats = convertIntArray(stats);
		DinoItem newDino = datasource.createDinoItem(name, 
				new Date(), 1, 0, bytStats, colorMain, colorAccent1, colorAccent2, -1);
		dinoItems.add(newDino);
		
		startActivity(intent);
	}
	// Cancel creation, return to summary
	public void cancelAdd(View v) {
		Intent intent = new Intent(CreateDinoActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
	
	// Int array conversion to byte array
	public static byte[] convertIntArray(int[] intArr) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		for(int i : intArr)
		{
			dos.writeInt(i);
		}
		dos.close();
		return baos.toByteArray();
	}

}
