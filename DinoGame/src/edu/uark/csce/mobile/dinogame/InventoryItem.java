package edu.uark.csce.mobile.dinogame;

import android.graphics.Bitmap;

public class InventoryItem {

	private long id;
	private String name;
	private byte[] statEffects;
	private byte[] icon;
	
	public InventoryItem() {
		id = 1;
		name = "Item";
		statEffects = null;
		icon = null;
	}
	
	public InventoryItem(long mID, String mName, byte[] mStats, byte[] mIcon) {
		id = mID;
		name = mName;
		statEffects = mStats;
		icon = mIcon;
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
	
}
