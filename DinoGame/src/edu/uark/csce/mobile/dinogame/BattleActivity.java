package edu.uark.csce.mobile.dinogame;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BattleActivity extends Activity {
	
	// Database
	private DinosDataSource datasource;
	List<DinoItem> dinoItems;
	
	// Dino info
	private int position;
	private DinoItem dino;
	private int level, experience;
	private ArrayList<Integer> stats;
	private int hp, attack, defense, special, spCount = 0, spMax;
	private boolean equipped;
	private int[] color = {0, 0, 0};
	
	// CPU info
	private int AIhp, AIspCount = 0;
	private Random move;
	
	// Info views
	private TextView HPText;
	private TextView aiHPText;
	private TextView SPText;
	private TextView aiSPText;
	private ImageView dinoPic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battle);
		
		datasource = new DinosDataSource(this);
		datasource.open();
		
		// Store the dinos in list
		dinoItems = datasource.getAllDinos();
		
		// Initialize text views
		HPText = (TextView)findViewById(R.id.playerHP);
		aiHPText = (TextView)findViewById(R.id.aiHP);
		SPText = (TextView)findViewById(R.id.playerSP);
		aiSPText = (TextView)findViewById(R.id.aiSP);
		
		move = new Random();

		// Retrieve dino info
		Intent intent = getIntent();
		position = intent.getIntExtra(SummaryActivity.EXTRA_POSITION, 0);
		dino = dinoItems.get(position);
		level = dino.getmLevel();
		experience = dino.getmExperience();
		stats = new ArrayList<Integer>();
		try {
			convertBytes(dino.getmStats());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(dino.getmEquip() == -1)
			equipped = false;
		setStats();
		updateText();
		drawDinoBitmap();
		drawAIDino();
	}
	
	// Button listeners
	public void attack(View v) {
		// Determine AI move
		int aiMove = move.nextInt(2);
		boolean spMove = false;
		if(AIspCount >= spMax) {
			aiMove = 0;
			spMove = true;
			AIspCount = 0;
		}
		// Calculate power based on move
		// 0 = attack, 1 = block
		if(aiMove == 0 && spMove == true) {	// AI performs special attack
			Toast.makeText(this, "Special", Toast.LENGTH_SHORT).show();
			AIspCount = 0;
			int aiDmg = attack*2 - (int)Math.floor((double)(defense/2));
			int playerDmg = attack - (int)Math.floor((double)(defense/2));
			if(aiDmg <= 0)
				aiDmg = 1;
			if(playerDmg <= 0)
				playerDmg = 1;
			AIhp = AIhp - playerDmg;
			hp = hp - aiDmg;
			if(AIhp <= 0) {
				AIhp = 0;
				victory();
			} else if(hp <= 0) {
				hp = 0;
				lose();
			}
		} else if(aiMove == 0) {
			Toast.makeText(this, "Attack", Toast.LENGTH_SHORT).show();
			int aiDmg = attack - (int)Math.floor((double)(defense/2));
			int playerDmg = attack - (int)Math.floor((double)(defense/2));
			if(aiDmg <= 0)
				aiDmg = 1;
			if(playerDmg <= 0)
				playerDmg = 1;
			AIhp = AIhp - playerDmg;
			hp = hp - aiDmg;
			if(AIhp <= 0) {
				AIhp = 0;
				victory();
			} else if(hp <= 0) {
				hp = 0;
				lose();
			}
		} else if(aiMove == 1) {
			Toast.makeText(this, "Block", Toast.LENGTH_SHORT).show();
			int playerDmg = attack - defense;
			if(playerDmg <= 0)
				playerDmg = 1;
			int spAdd = attack - playerDmg;
			if(spAdd <= 0)
				spAdd = 1;
			AIspCount += spAdd;
			if(AIspCount > spMax)
				AIspCount = spMax;
			AIhp = AIhp - playerDmg;
			if(AIhp <= 0) {
				AIhp = 0;
				victory();
			}
		}
		updateText();
	}
	
	public void block(View v) {
		// Determine AI move
		int aiMove = move.nextInt(2);
		boolean spMove = false;
		if(AIspCount >= spMax) {
			aiMove = 0;
			spMove = true;
			AIspCount = 0;
		}
		// Calculate power based on move
		// 0 = attack, 1 = block
		if(aiMove == 0 && spMove == true) {	// AI performs special attack
			Toast.makeText(this, "Special", Toast.LENGTH_SHORT).show();
			AIspCount = 0;
			int aiDmg = attack*2 - defense;
			if(aiDmg <= 0)
				aiDmg = 1;
			int spAdd = attack - aiDmg;
			if(spAdd <= 0)
				spAdd = 1;
			spCount += spAdd;
			if(spCount > spMax)
				spCount = spMax;
			hp = hp - aiDmg;
			if(hp <= 0) {
				hp = 0;
				lose();
			}
		} else if(aiMove == 0) {
			Toast.makeText(this, "Attack", Toast.LENGTH_SHORT).show();
			int aiDmg = attack - defense;
			if(aiDmg <= 0)
				aiDmg = 1;
			int spAdd = attack - aiDmg;
			if(spAdd <= 0)
				spAdd = 1;
			spCount += spAdd;
			if(spCount > spMax)
				spCount = spMax;
			hp = hp - aiDmg;
			if(hp <= 0) {
				hp = 0;
				lose();
			}
		} else if(aiMove == 1) {
			Toast.makeText(this, "Block", Toast.LENGTH_SHORT).show();
			// do nothing because both blocked
		}
		updateText();
	}
	
	public void special(View v) {
		if(spCount >= spMax) {
			spCount = 0;
			// Determine AI move
			int aiMove = move.nextInt(2);
			boolean spMove = false;
			if(AIspCount >= spMax) {
				aiMove = 0;
				spMove = true;
				AIspCount = 0;
			}
			// Calculate power based on move
			// 0 = attack, 1 = block
			if(aiMove == 0 && spMove == true) {	// AI performs special attack
				Toast.makeText(this, "Special", Toast.LENGTH_SHORT).show();
				AIspCount = 0;
				int aiDmg = attack*2 - (int)Math.floor((double)(defense/2));
				int playerDmg = attack*2 - (int)Math.floor((double)(defense/2));
				if(aiDmg <= 0)
					aiDmg = 1;
				if(playerDmg <= 0)
					playerDmg = 1;
				AIhp = AIhp - playerDmg;
				hp = hp - aiDmg;
				if(AIhp <= 0) {
					AIhp = 0;
					victory();
				} else if(hp <= 0) {
					hp = 0;
					lose();
				}
			} else if(aiMove == 0) {
				Toast.makeText(this, "Attack", Toast.LENGTH_SHORT).show();
				int aiDmg = attack - (int)Math.floor((double)(defense/2));
				int playerDmg = attack*2 - (int)Math.floor((double)(defense/2));
				if(aiDmg <= 0)
					aiDmg = 1;
				if(playerDmg <= 0)
					playerDmg = 1;
				AIhp = AIhp - playerDmg;
				hp = hp - aiDmg;
				if(AIhp <= 0) {
					AIhp = 0;
					victory();
				} else if(hp <= 0) {
					hp = 0;
					lose();
				}
			} else if(aiMove == 1) {
				Toast.makeText(this, "Block", Toast.LENGTH_SHORT).show();
				int playerDmg = attack*2 - defense;
				if(playerDmg <= 0)
					playerDmg = 1;
				int spAdd = attack - playerDmg;
				if(spAdd <= 0)
					spAdd = 1;
				AIspCount += spAdd;
				if(AIspCount > spMax)
					AIspCount = spMax;
				AIhp = AIhp - playerDmg;
				if(AIhp <= 0) {
					AIhp = 0;
					victory();
				}
			}
			updateText();
		} else {
			Toast.makeText(this, "Special not charged, try blocking", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void forfeit(View v) {
		Intent intent = new Intent(BattleActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
	
	// Battle state functions
	public void victory() {
		Toast.makeText(this, "You won!", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(BattleActivity.this, SummaryActivity.class);
		startActivity(intent);
	}
	
	public void lose() {
		Toast.makeText(this, "You lost!", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(BattleActivity.this, SummaryActivity.class);
		startActivity(intent);
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
	
	// Initialize stats and mechanics
	private void setStats() {
		hp = 10 + (level * 5);
		AIhp = hp;
		attack = stats.get(0);
		defense = stats.get(1);
		special = stats.get(2);
		spMax = (level * 3) - special;
		
	}
	
	// Update text views with stats
	private void updateText() {
		HPText.setText(Integer.toString(hp));
		aiHPText.setText(Integer.toString(AIhp));
		SPText.setText(Integer.toString(spCount)+"/"+Integer.toString(spMax));
		aiSPText.setText(Integer.toString(AIspCount)+"/"+Integer.toString(spMax));
	}
	
	// Draws a dino character on screen from a bitmap
    private void drawDinoBitmap() {
    	
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
	    dinoPic = (ImageView)findViewById(R.id.playerDino);
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
    
    private void drawAIDino() {
    	
    }

}
