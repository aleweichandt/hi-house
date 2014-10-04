package com.web.ones.hihouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileInfoFragment extends Fragment implements
OnClickListener,
OnItemClickListener{
	final static String ARG_NAME = "name";
	final static String ARG_IS_ADD = "isAddOperation";
	private boolean mIsAddOperation = false;
	private boolean mState = false;
	private String mName;
	private View mMainView;
	private HiHouse hiHouseAct;
	ListView lv;
	private ArrayList<Device> devices;
	private EditText prof_name;

	public ProfileInfoFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		hiHouseAct = (HiHouse)getActivity();
		
		Bundle args = getArguments();
		if (args != null){
			mName = args.getString(ARG_NAME, "Nuevo");
			mIsAddOperation = args.getBoolean(ARG_IS_ADD);
			mState = mIsAddOperation;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.fragment_profile_info, container, false);
		
		lv = (ListView) mMainView.findViewById(R.id.profileinfo_devices);
		prof_name = (EditText)mMainView.findViewById(R.id.profileinfo_name);
		prof_name.setText(mName);
		
		setEditMode(mIsAddOperation || mState);
		
		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.GET_ALL_DEVICES, true, "devices/all", "token="+hiHouseAct.getUser().getToken()));
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
		
		return mMainView;
	}
	
	public void loadDevices(String str) {
		devices = new ArrayList<Device>();
		final ArrayList<String> list = new ArrayList<String>();
		JSONArray devArray;
		JSONObject deviceInfo;
    	try{
    		devArray = new JSONArray(str);
    		for(int i=0; i<devArray.length(); i++){
    			deviceInfo = devArray.getJSONObject(i);
    			Device d = new Device(deviceInfo.getString("id"), deviceInfo.getString("name"));
    			d.setState(false); //estado del checkbox
    			devices.add(d);
    		}
    	}
    	catch(JSONException e){
    		e.printStackTrace();
    	}
    	
		ProfileInfoAdapter adapter = new ProfileInfoAdapter(getActivity());
		lv.setAdapter(adapter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		setListeners(null);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setListeners(this);
	}
	
	private void setListeners(OnClickListener listener) {
		mMainView.findViewById(R.id.profileinfo_confirm).setOnClickListener(listener);
		mMainView.findViewById(R.id.profileinfo_cancel).setOnClickListener(listener);
		mMainView.findViewById(R.id.profileinfo_edit).setOnClickListener(listener);
		mMainView.findViewById(R.id.profileinfo_delete).setOnClickListener(listener);
		lv.setOnItemClickListener((OnItemClickListener) listener);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case (R.id.profileinfo_confirm):
			onConfirmPressed();
			break;
		case (R.id.profileinfo_cancel):
			onCancelPressed();
			break;
		case (R.id.profileinfo_edit):
			onEditPressed();
			break;
		case (R.id.profileinfo_delete):
			onDeletePressed();
			break;
		default:
			break;
		}
		
	}

	private void onEditPressed() {
		setEditMode(true);
	}
	
	private void onConfirmPressed() {
		String profName = prof_name.getText().toString();
		JSONObject builder = new JSONObject();
		JSONArray devArray = new JSONArray();
		try{
			builder.put("name", profName);
			builder.put("description", "blabla");//TODO desc de donde? agregar campo?
			for(Device d : devices){
				if(d.getState())
					devArray.put(d.getId());
			}
			builder.put("devices", devArray);
		}
		catch (JSONException e){}
		
		//TODO revisar ID
		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.ADD_PROFILE, false, "profiles/"+profName.toLowerCase().replace(" ", "")+"?token="+hiHouseAct.getUser().getToken(), builder.toString()));
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
		
	}
	
	private void onCancelPressed() {
		//TODO rollback changes
		if(mIsAddOperation) {
			getActivity().getFragmentManager().popBackStack();
			return;
		}
		setEditMode(false);
	}
	
	private void onDeletePressed() {
		//TODO ask before
		//TODO remove user
		getActivity().getFragmentManager().popBackStack();
	}
	
	private void setEditMode(boolean on) {
		mMainView.findViewById(R.id.profileinfo_name).setEnabled(on);
		mMainView.findViewById(R.id.profileinfo_edit).setVisibility(on?View.GONE:View.VISIBLE);
		mMainView.findViewById(R.id.profileinfo_delete).setVisibility(on?View.GONE:View.VISIBLE);
		mMainView.findViewById(R.id.profileinfo_confirm).setVisibility(on?View.VISIBLE:View.GONE);
		mMainView.findViewById(R.id.profileinfo_cancel).setVisibility(on?View.VISIBLE:View.GONE);
		mState = on;
	}

	@Override
	public void onItemClick(AdapterView<?> ad, View v, int pos, long id) {
		CheckBox r = (CheckBox)v.findViewById(R.id.dev_row_check);
		boolean b = !r.isChecked();
		r.setChecked(b);
		devices.get(pos).setState(b);
	}
	
	//adapter for list
	private class ProfileInfoAdapter extends ArrayAdapter<Device> {
		private Context mContext;
		
		public ProfileInfoAdapter(Context context) {
			super(context, R.layout.profileinfo_device_row, devices);
			mContext = context;
		}
			
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.profileinfo_device_row, parent, false);
			TextView tv = (TextView)rowView.findViewById(R.id.dev_row_name);
			tv.setText(devices.get(position).getName());
			CheckBox cb = (CheckBox)rowView.findViewById(R.id.dev_row_check);
			cb.setChecked(devices.get(position).getState());
			return rowView;
		}
	}

	public void addProfileResult(boolean b) {
		if(!b) {
			Toast.makeText(getActivity(), "Un perfil con ese nombre ya existe.", Toast.LENGTH_LONG).show();
			return;
		}
		if(mIsAddOperation) {
			Toast.makeText(getActivity(), "Perfil agregado exitosamente.", Toast.LENGTH_LONG).show();
			getActivity().getFragmentManager().popBackStack();
			return;
		}
		setEditMode(false);
	}
}
