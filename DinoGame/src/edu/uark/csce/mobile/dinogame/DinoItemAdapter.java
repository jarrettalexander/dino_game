package edu.uark.csce.mobile.dinogame;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DinoItemAdapter extends ArrayAdapter<DinoItem>{
	int resource;

	public DinoItemAdapter(Context context, int resource, List<DinoItem> items){
		super(context, resource, items);
		this.resource = resource;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		LinearLayout itemView;
		DinoItem item = getItem(position);
		
		String name = item.getmName();
		int level = item.getmLevel();
		int exp = item.getmExperience();
		
		if(convertView == null){
			itemView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater lifr = (LayoutInflater)getContext().getSystemService(inflater);
			lifr.inflate(resource, itemView, true);
		}
		else {
			itemView = (LinearLayout)convertView;
		}
		
		TextView nameView = (TextView)itemView.findViewById(R.id.rowName);
		TextView lvlView = (TextView)itemView.findViewById(R.id.rowLevel);
		TextView expView = (TextView)itemView.findViewById(R.id.rowExp);
		
		nameView.setText(name);
		lvlView.setText("Level: " + level);
		expView.setText("Exp: " + exp);
		return itemView;
	}
}
