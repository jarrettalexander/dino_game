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
	private int mAttack;
	private int mDefense;
	private int mSpecial;
	private byte[] mImage;

	public DinoItem() {
		// Initialize first workout
		mID = 1;
		mName = "Dino";
//		mCreatedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		mCreatedDate = new Date();
		mLevel = 0;
		mExperience = 0;
		mAttack = 0;
		mDefense = 0;
		mSpecial = 0;
		mImage = null;
	}
	
	public DinoItem(long ID, String name, Date date, int level, int experience, int attack, int defense, int special, byte[] image) {
		// Initialize first dino
		mID = ID;
		mName = name;
		mCreatedDate = date;
		mLevel = level;
		mExperience = experience;
		mAttack = attack;
		mDefense = defense;
		mSpecial = special;
		mImage = image;
	}
	
	public static void convertList(byte[] asBytes, ArrayList<Double> list) throws IOException {

	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    DataOutputStream dout = new DataOutputStream(bout);
	    for (double d : list) {
	        dout.writeDouble(d);
	    }
	    dout.close();
	    asBytes = bout.toByteArray();
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

	public int getmAttack() {
		return mAttack;
	}

	public void setmAttack(int mAttack) {
		this.mAttack = mAttack;
	}

	public int getmDefense() {
		return mDefense;
	}

	public void setmDefense(int mDefense) {
		this.mDefense = mDefense;
	}

	public int getmSpecial() {
		return mSpecial;
	}

	public void setmSpecial(int mSpecial) {
		this.mSpecial = mSpecial;
	}

	public byte[] getmImage() {
		return mImage;
	}

	public void setmImage(byte[] mImage) {
		this.mImage = mImage;
	}
	
}
