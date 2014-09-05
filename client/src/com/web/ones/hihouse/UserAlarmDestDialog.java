package com.web.ones.hihouse;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class UserAlarmDestDialog extends DialogFragment{
	
	private OnAlarmDestListener mListener;
	private Spinner mUserSpinner;
	
	public UserAlarmDestDialog(OnAlarmDestListener listener) {
		super();
		mListener = listener;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstance) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.popup_alarm_dest_title)
	           .setView( (View)createSpinnerView() )
	           .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   if(mListener != null) {
	            		   mListener.OnAlarmDestConfirm((String)mUserSpinner.getSelectedItem());
	            	   }
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if(mListener != null) {
		            		   mListener.OnAlarmDestCancel((String)mUserSpinner.getSelectedItem());
		            	}
					}
	           });

	    return builder.create();
	}
	
	private View createSpinnerView() {
		String[] values = new String[] { "Jose", "Ines", "Juancito",
		        "Pedro", "Betina" };

	    final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < values.length; ++i) {
	      list.add(values[i]);
	    }
	    
		mUserSpinner = new Spinner(getActivity());
		mUserSpinner.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mUserSpinner.setAdapter(new ArrayAdapter<String>(getActivity(),
								android.R.layout.simple_spinner_item,
								list));
		return (View)mUserSpinner;
	}
	
	public interface OnAlarmDestListener {
		public void OnAlarmDestConfirm(String userid);
		public void OnAlarmDestCancel(String userid);
	}
}
