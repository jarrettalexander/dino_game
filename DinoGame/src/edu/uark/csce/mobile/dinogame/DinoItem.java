package edu.uark.csce.mobile.dinogame;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DinoItem {
	
	private long mID;
	private String mName;
	private Date mCreatedDate;
	private int mLevel;
	private int mExperience;
	private byte[] mStats;
	private int mColorMain;
	private int mColorAccent1;
	private int mColorAccent2;
	private long mEquip = -1;

	public DinoItem() {
		// Initialize dino
		mID = 1;
		mName = "Dino";
		mCreatedDate = new Date();
		mLevel = 0;
		mExperience = 0;
		mStats = null;
		mColorMain = 0xFF00FF00;
		mColorAccent1 = 0xFFFF0000;
		mColorAccent2 = 0xFF0000FF;
		mEquip = -1;
	}
	
	public DinoItem(long ID, String name, Date date, int level, int experience, byte[] stats, int colorMain, int colorAccent1, int colorAccent2, long equip) {
		// Initialize first dino
		mID = ID;
		mName = name;
		mCreatedDate = date;
		mLevel = level;
		mExperience = experience;
		mStats = stats;
		mColorMain = colorMain;
		mColorAccent1 = colorAccent1;
		mColorAccent2 = colorAccent2;
		mEquip = equip;
	}
	
	@Override
	public String toString() {
		return mName;
	}

	public long getmID() {
		return mID;
	}

	public void setmID(long mID) {
		this.mID = mID;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public Date getmCreatedDate() {
		return mCreatedDate;
	}

	public void setmCreatedDate(String mCreatedDate) {
		try {
			this.mCreatedDate = new SimpleDateFormat("yyyy-MM-dd").parse(mCreatedDate);
		} catch (ParseException e){
			e.printStackTrace();
		}
	}

	public int getmLevel() {
		return mLevel;
	}

	public void setmLevel(int mLevel) {
		this.mLevel = mLevel;
	}

	public int getmExperience() {
		return mExperience;
	}

	public void setmExperience(int mExperience) {
		this.mExperience = mExperience;
	}

	public byte[] getmStats() {
		return mStats;
	}

	public void setmStats(byte[] mStats) {
		this.mStats = mStats;
	}

	public int getColorMain() {
		return mColorMain;
	}

	public void setColorMain(int mColor) {
		this.mColorMain = mColor;
	}
	
	public int getColorAccent1() {
		return this.mColorAccent1;
	}
	
	public void setColorAccent1(int color) {
		this.mColorAccent1 = color;
	}
	
	public int getColorAccent2() {
		return this.mColorAccent2;
	}
	
	public void setColorAccent2(int color) {
		this.mColorAccent2 = color;
	}
 
	public long getmEquip() {
		return mEquip;
	}

	public void setmEquip(long mEquip) {
		this.mEquip = mEquip;
	}
	
}
