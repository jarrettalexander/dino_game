package edu.uark.csce.mobile.dinogame;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class CreateDinoActivity extends Activity {
	
	// Edit fields
	private EditText nameText;
	private RadioGroup colorChoice;
	private RadioButton choice;
	
	// Database stuff
	private DinosDataSource datasource;
	private List<DinoItem> dinoItems;
	
	// Dino stuff
	private String name;
	private String colorText;
	private int[] stats = {1, 1, 1};
	private byte[] bytStats;
	private int color;
	
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
		color = 0;
		
		// Register text fields
		nameText = (EditText)findViewById(R.id.nameField);
		colorChoice = (RadioGroup)findViewById(R.id.colorField);
		choice = (RadioButton)findViewById(colorChoice.getCheckedRadioButtonId());
	}
	
	// Button listeners
	public void saveDino(View v) throws IOException {
		Intent intent = new Intent(CreateDinoActivity.this, SummaryActivity.class);
		// Set character values
		name = nameText.getText().toString();
		colorText = choice.getText().toString();
		if(colorText.equalsIgnoreCase("red"))
			color = 1;
		else if(colorText.equalsIgnoreCase("blue"))
			color = 2;
		else if(colorText.equalsIgnoreCase("green"))
			color = 3;
		
		bytStats = convertIntArray(stats);
		DinoItem newWorkout = datasource.createDinoItem(name, 
				new Date(), 1, 0, bytStats, color, -1);
		dinoItems.add(newWorkout);
		
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
