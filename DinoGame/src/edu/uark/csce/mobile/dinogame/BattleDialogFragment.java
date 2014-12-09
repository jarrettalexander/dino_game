package edu.uark.csce.mobile.dinogame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class BattleDialogFragment extends DialogFragment {
	
	private final String winTitle = "Victory!";
	private final String losTitle = "Defeat";
	private final String lvlMessage = "Congratulations! Your dino was victorious in battle! As a reward " +
			"your dino has been given stat boosts and 50 experience.";
	private final String winMessage = "Congratulations! Your dino was victorious in battle! As a reward " +
			"your dino has been given 50 experience.";
	private final String losMessage = "Ouch! Unfortunately your dino lost in battle. No worries though, it " +
			"hasn't died! For your dino's efforts it's been rewarded 20 experience.";
	
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface BattleDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }
    
    // Use this instance of the interface to deliver action events
    BattleDialogListener mListener;
    
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (BattleDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
    	boolean lvlUp = getArguments().getBoolean("lvl");
    	boolean win = getArguments().getBoolean("win");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(lvlUp == true && win == true) {
        	builder.setTitle(winTitle)
        	.setMessage(lvlMessage)
        	.setPositiveButton("Yay!", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			// Send the positive button event back to the host activity
        			mListener.onDialogPositiveClick(BattleDialogFragment.this);
        		}
        	});
        	return builder.create();
        } else if(lvlUp == false && win == true) {
        	builder.setTitle(winTitle)
        	.setMessage(winMessage)
        	.setPositiveButton("Yay!", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			// Send the positive button event back to the host activity
        			mListener.onDialogPositiveClick(BattleDialogFragment.this);
        		}
        	});
        	return builder.create();
        } else {
        	builder.setTitle(losTitle)
        	.setMessage(losMessage)
        	.setPositiveButton("Aww Okay...", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			// Send the positive button event back to the host activity
        			mListener.onDialogPositiveClick(BattleDialogFragment.this);
        		}
        	});
        	return builder.create();
        }
    }

}
