package edu.uark.csce.mobile.dinogame;

import android.graphics.Bitmap;

public class InventoryItem {

	private long id;
	private String name;
	private byte[] statEffects;
	private byte[] icon;
	private int colorMain;
	private int colorAccent1;
	private int colorAccent2;
	
	public InventoryItem() {
		id = 1;
		name = "Item";
		statEffects = null;
		icon = null;
		colorMain = 0xFFFF0000;
		colorAccent1 = 0xFF00FF00;
		colorAccent2 = 0xFF0000FF;
	}
	
	public InventoryItem(long mID, String mName, byte[] mStats, byte[] mIcon, int colorMain, int colorAccent1, int colorAccent2) {
		id = mID;
		name = mName;
		statEffects = mStats;
		icon = mIcon;
		this.colorMain = colorMain;
		this.colorAccent1 = colorAccent1;
		this.colorAccent2 = colorAccent2;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public byte[] getStatEffects() {
		return statEffects;
	}

	public void setStatEffects(byte[] statEffects) {
		this.statEffects = statEffects;
	}

	public byte[] getIcon() {
		return icon;
	}

	public void setIcon(byte[] icon) {
		this.icon = icon;
	}
	
	public int getColorMain() {
		return this.colorMain;
	}
	
	public void setColorMain(int color) {
		this.colorMain = color;
	}
	
	public int getColorAccent1() {
		return this.colorAccent1;
	}
	
	public void setColorAccent1(int color) {
		this.colorAccent1 = color;
	}
	
	public int getColorAccent2() {
		return this.colorAccent1;
	}
	
	public void setColorAccent2(int color) {
		this.colorAccent2 = color;
	}
	
	
}
