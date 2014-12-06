package edu.uark.csce.mobile.dinogame;

import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreateDinoActivity extends Activity {
	
	// Edit fields
	private EditText nameText;
	
	// Database stuff
	private DinosDataSource datasource;
	private List<DinoItem> dinoItems;
	
	// Dino stuff
	private String name;
	private int baseAttack;
	private int baseDefense;
	private int baseSpecial;
	byte[] image;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		
		datasource = new DinosDataSource(this);
		datasource.open();
		
		// Store the dinos in list
		dinoItems = datasource.getAllDinos();
		
		name = null;
		baseAttack = 0;
		baseDefense = 0;
		baseSpecial = 0;
		image = null;
		
		// Register text fields
		nameText = (EditText)findViewById(R.id.nameField);
	}
	
	// Button listeners
	public void saveDino(View v) {
		Intent intent = new Intent(CreateDinoActivity.this, SummaryActivity.class);
		// Set character values
		name = nameText.getText().toString();
		
		DinoItem newWorkout = datasource.createDinoItem(name, 
				new Date(), 1, 0, baseAttack, baseDefense, baseSpecial, image);
		dinoItems.add(newWorkout);
		
		startActivity(intent);
	}

}
