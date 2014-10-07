package com.web.ones.hihouse;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.NumberPicker;

public class PickerDialog extends DialogFragment {
	public static final String PICKER_MIN_VALUE = "PickerDialog.min";
	public static final String PICKER_MAX_VALUE = "PickerDialog.max";
	public static final String PICKER_CURRENT_VALUE = "PickerDialog.current";
	
	private OnPickerDialogListener mListener;
	private Bundle mBundle;
	private NumberPicker mPicker;
	
	public PickerDialog(OnPickerDialogListener listener, Bundle b) {
		super();
		mListener = listener;
		mBundle = b;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstance) {
		int minValue=0, maxValue=0, currentValue=0;
		setCancelable(false);
		if(savedInstance == null) {
			savedInstance = mBundle;
		}
		if(savedInstance != null) {
			//load bundle
			if(savedInstance.containsKey(PICKER_MIN_VALUE)) {
				minValue = savedInstance.getInt(PICKER_MIN_VALUE);
				currentValue = minValue; //safety check
			}
			if(savedInstance.containsKey(PICKER_MAX_VALUE)) {
				maxValue = savedInstance.getInt(PICKER_MAX_VALUE);
			}
			if(savedInstance.containsKey(PICKER_CURRENT_VALUE)) {
				currentValue = savedInstance.getInt(PICKER_CURRENT_VALUE);
			}
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.userinfo_profiles)
	           .setView( (View)createPickerView(minValue, maxValue, currentValue) )
	           .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   if(mListener != null) {
	            		   mListener.OnPickerConfirm(mPicker.getValue());
	            	   }
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if(mListener != null) {
		            		   mListener.OnPickerCancel(mPicker.getValue());
		            	}
					}
	           });

	    return builder.create();
	}
	
	private View createPickerView(int minValue, int maxValue, int currentValue) {
		mPicker = new NumberPicker(getActivity());
		mPicker.setMaxValue(maxValue);
		mPicker.setMinValue(minValue);
		mPicker.setValue(currentValue);
		mPicker.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		return (View)mPicker;
	}
	
	public interface OnPickerDialogListener {
		public void OnPickerConfirm(int value);
		public void OnPickerCancel(int value);
	}
}
