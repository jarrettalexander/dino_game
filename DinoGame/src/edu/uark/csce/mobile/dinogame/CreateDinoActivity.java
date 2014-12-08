package edu.uark.csce.mobile.dinogame;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class CreateDinoActivity extends Activity {
	
	// Edit fields
	private EditText nameText;
	//private RadioGroup colorChoice;
	//private RadioButton choice;
	
	private TextView colorTextView1;
	private TextView colorTextView2;
	private TextView colorTextView3;
	
	private Button buttonChooseColor1;
	private Button buttonChooseColor2;
	private Button buttonChooseColor3;
	
	// Database stuff
	private DinosDataSource datasource;
	private List<DinoItem> dinoItems;
	
	// Dino stuff
	private String name;
	private String colorText;
	private int[] stats = {1, 1, 1};
	private byte[] bytStats;
	private int colorMain;
	private int colorAccent1;
	private int colorAccent2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		
		datasource = new DinosDataSource(this);
		datasource.open();
		
		// Store the dinos in list
		dinoItems = datasource.getAllDinos();
		
		name = null;
		bytStats = null;
		colorMain = 0xFFFF0000;
		colorAccent1 = 0xFF00FF00;
		colorAccent2 = 0xFF0000FF;
		
		// Register text fields
		nameText = (EditText)findViewById(R.id.nameField);
		//colorChoice = (RadioGroup)findViewById(R.id.colorField);
		//choice = (RadioButton)findViewById(colorChoice.getCheckedRadioButtonId());
		
		colorTextView1 = (TextView)findViewById(R.id.textColor1);
		colorTextView2 = (TextView)findViewById(R.id.textColor2);
		colorTextView3 = (TextView)findViewById(R.id.textColor3);
		
		buttonChooseColor1 = (Button)findViewById(R.id.buttonChooseColor1);
		buttonChooseColor1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openColorDialog1(v);
			}
		});
		
		buttonChooseColor2 = (Button)findViewById(R.id.buttonChooseColor2);
		buttonChooseColor2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openColorDialog2(v);
			}
		});
		
		buttonChooseColor3 = (Button)findViewById(R.id.buttonChooseColor3);
		buttonChooseColor3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openColorDialog3(v);
			}
		});
		
	}
	
	// Button listeners
	public void openColorDialog1(View v) {
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(CreateDinoActivity.this, 0xffffff00, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
			@Override
			public void onOk(AmbilWarnaDialog dialog, int col) {
				//Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
				
				colorMain = col;
				colorTextView1.setText(Integer.toString(col));
			}

			@Override
			public void onCancel(AmbilWarnaDialog dialog) {
				//Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
			}
		});
		dialog.show();
	}
	
	public void openColorDialog2(View v) {
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(CreateDinoActivity.this, 0xffffff00, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
			@Override
			public void onOk(AmbilWarnaDialog dialog, int col) {
				//Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
				
				colorAccent1 = col;
				colorTextView2.setText(Integer.toString(col));
			}

			@Override
			public void onCancel(AmbilWarnaDialog dialog) {
				//Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
			}
		});
		dialog.show();
	}
	
	public void openColorDialog3(View v) {
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(CreateDinoActivity.this, 0xffffff00, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
			@Override
			public void onOk(AmbilWarnaDialog dialog, int col) {
				//Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
				
				colorAccent2 = col;
				colorTextView3.setText(Integer.toString(col));
			}

			@Override
			public void onCancel(AmbilWarnaDialog dialog) {
				//Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
			}
		});
		dialog.show();
	}
	
	public void saveDino(View v) throws IOException {
		Intent intent = new Intent(CreateDinoActivity.this, SummaryActivity.class);
		// Set character values
		name = nameText.getText().toString();
//		colorText = choice.getText().toString();
//		if(colorText.equalsIgnoreCase("red"))
//			color = 1;
//		else if(colorText.equalsIgnoreCase("blue"))
//			color = 2;
//		else if(colorText.equalsIgnoreCase("green"))
//			color = 3;
		
		//Log.d(GeofenceUtils.APPTAG, "colors are: " + Integer.toHexString(colorMain) + ", " + Integer.toHexString(colorAccent1) + ", " + Integer.toHexString(colorAccent2));
		
		bytStats = convertIntArray(stats);
		DinoItem newDino = datasource.createDinoItem(name, 
				new Date(), 1, 0, bytStats, colorMain, colorAccent1, colorAccent2, -1);
		dinoItems.add(newDino);
		
		startActivity(intent);
	}
	
	public void cancelAdd(View v) {
		Intent intent = new Intent(CreateDinoActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
	
	// Byte array conversion
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
