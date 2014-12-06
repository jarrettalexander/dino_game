package edu.uark.csce.mobile.dinogame;

import android.graphics.Bitmap;

public class InventoryItem {

	private Long id;
	private String name;
	private Bitmap icon;
	private int[] statEffects;
	
	public InventoryItem() {}

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

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

	public int[] getStatEffects() {
		return statEffects;
	}

	public void setStatEffects(int[] statEffects) {
		this.statEffects = statEffects;
	}
	
}
