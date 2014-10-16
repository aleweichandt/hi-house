package com.web.ones.hihouse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class UserAlarmDestDialog extends DialogFragment implements OnItemSelectedListener{
	
	private OnAlarmDestListener callback;
	private Spinner mUserSpinner;
	private ArrayList<User> users;
	private int selectedUserPos;
	
	public UserAlarmDestDialog() {
		super();
		callback = null;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
             callback = (OnAlarmDestListener) getTargetFragment();
         } catch (ClassCastException e) {
             throw new ClassCastException("Calling Fragment must implement OnAddFriendListener"); 
         }
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstance) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.popup_alarm_dest_title)
	           .setView( (View)createSpinnerView() )
	           .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   if(callback != null) {
	            		   callback.OnAlarmDestConfirm(users.get(selectedUserPos).getId());
	            	   }
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						getDialog().cancel();
					}
	           });

	    ((HiHouse)getActivity()).mHiHouseService.sendCommand(new Command(Request.GET_LIST_USERS, true, "users/all?token="+((HiHouse)getActivity()).getUser().getToken(), ""));
		
	    return builder.create();
	}
	
	private View createSpinnerView() {
	    View pb = getActivity().getLayoutInflater().inflate(R.layout.alarm_dialog, null);
		return pb;
	}
	
	public interface OnAlarmDestListener {
		public void OnAlarmDestConfirm(int userid);
	}
	
	public void loadUserList(String str){		
		users = new ArrayList<User>();
		JSONArray devArray;
		JSONObject userInfo;
    	try{
    		devArray = new JSONArray(str);
    		for(int i=0; i<devArray.length(); i++){
    			userInfo = devArray.getJSONObject(i);
    			User d = new User(userInfo.getInt("id"), userInfo.getString("name"));
    			users.add(d);
    		}
    	}
    	catch(JSONException e){e.printStackTrace();}
    	
    	LinearLayout l = (LinearLayout)this.getDialog().findViewById(R.id.alarm_dialog);
		l.removeAllViews();
		
		mUserSpinner = new Spinner(getActivity());
		mUserSpinner.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mUserSpinner.setAdapter(new UserListAdapter(getActivity()));
		mUserSpinner.setOnItemSelectedListener(this);
		
		l.addView(mUserSpinner);
	}
	
	private class UserListAdapter extends ArrayAdapter<User> {
  		private Context mContext;
  		
  		public UserListAdapter(Context context) {
  			super(context, R.layout.simple_row, users);
  			mContext = context;
  		}
  		
  		@Override
		public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
			return getCustomView(position, cnvtView, prnt);
		}
  		@Override
		public View getView(int pos, View cnvtView, ViewGroup prnt) {
			return getCustomView(pos, cnvtView, prnt);
		}
  			
  		public View getCustomView(int position, View convertView, ViewGroup parent) {
  			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  			View rowView = inflater.inflate(R.layout.simple_row, parent, false);
  			TextView tv = (TextView)rowView.findViewById(R.id.row_name);
  			tv.setText(users.get(position).getUser());
  			return rowView;
  		}
  		
  		@Override
  		public int getCount(){
  			return users.size();
  		}
  	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // profiles.get(pos).getId()
		selectedUserPos = pos;
    }
	public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
