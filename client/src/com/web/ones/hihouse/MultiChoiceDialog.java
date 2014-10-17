package com.web.ones.hihouse;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class MultiChoiceDialog extends DialogFragment {
	public static final String MULTICHOICHE_ALL = "MultiChoiceDialog.all";
	public static final String MULTICHOICHE_SELECTED = "MultiChoiceDialog.selected";
	
	private List<CharSequence> mSelectedItems;
	private CharSequence[] mAllItems = {};
	
	private OnMultiChoiceDialogListener mListener;
	private Bundle mBundle;
	
	public MultiChoiceDialog(OnMultiChoiceDialogListener listener, Bundle b) {
		super();
		
		mListener = null;
		mBundle = b;
	}
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		try{
			mListener = (OnMultiChoiceDialogListener) getTargetFragment();
		}
		catch (ClassCastException e){throw new ClassCastException("Calling fragment must implement OnMultiChoice");};
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstance) {
		mAllItems = new CharSequence[0];
		mSelectedItems = new ArrayList<CharSequence>();
		if(savedInstance == null) {
			savedInstance = mBundle;
		}
		if(savedInstance != null) {
			//load bundle
			if(savedInstance.containsKey(MULTICHOICHE_ALL)) {
				mAllItems = savedInstance.getCharSequenceArray(MULTICHOICHE_ALL);
			}
			if(savedInstance.containsKey(MULTICHOICHE_SELECTED)) {
				mSelectedItems = savedInstance.getCharSequenceArrayList(MULTICHOICHE_SELECTED);
			}
		}
		
		boolean[] selected = new boolean[mAllItems.length];
		for(int i=0;i<mAllItems.length;i++) {
			selected[i] = mSelectedItems.contains(mAllItems[i]);
		}
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.userinfo_profiles)
	           .setMultiChoiceItems(mAllItems, selected,
	                      new DialogInterface.OnMultiChoiceClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int which,
	                       boolean isChecked) {
	            	   CharSequence value = mAllItems[which];
	                   if (isChecked) {
	                       mSelectedItems.add(value);
	                   } else if (mSelectedItems.contains(value)) {
	                       mSelectedItems.remove(value);
	                   }
	               }
	           })
	           .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   List<CharSequence> a = mSelectedItems;
	            	   if(mListener != null) {
	            		   mListener.OnMultiChoiceConfirm(a);
	            	   }
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if(mListener != null) {
		            		   mListener.OnMultiChoiceCancel(mSelectedItems);
		            	}
					}
	           });

	    return builder.create();
	}
	
	public interface OnMultiChoiceDialogListener {
		public void OnMultiChoiceConfirm(List<CharSequence> selected);
		public void OnMultiChoiceCancel(List<CharSequence> selected);
	}
}
