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
	private int mColor;
	private int mEquip = -1;

	public DinoItem() {
		// Initialize first workout
		mID = 1;
		mName = "Dino";
		mCreatedDate = new Date();
		mLevel = 0;
		mExperience = 0;
		mStats = null;
		mColor = 0;
		mEquip = -1;
	}
	
	public DinoItem(long ID, String name, Date date, int level, int experience, byte[] stats, int color, int equip) {
		// Initialize first dino
		mID = ID;
		mName = name;
		mCreatedDate = date;
		mLevel = level;
		mExperience = experience;
		mStats = stats;
		mColor = color;
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

	public int getmColor() {
		return mColor;
	}

	public void setmColor(int mColor) {
		this.mColor = mColor;
	}

	public int getmEquip() {
		return mEquip;
	}

	public void setmEquip(int mEquip) {
		this.mEquip = mEquip;
	}
	
}
