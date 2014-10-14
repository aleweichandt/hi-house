package com.web.ones.hihouse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SimulatorFragment extends Fragment implements OnItemSelectedListener {
	
	private View mRootView;
	private LinearLayout simu_containter;
    private HiHouse hiHouseAct;
	private ArrayList<Profile> profiles;
	private ArrayList<Device> devices;
	private ProfileListAdapter mAdapter;
	private int selectedProfilePos;
	private Button btn_accept, btn_cancel;

	public SimulatorFragment() {
        // Empty constructor required for fragment subclasses
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hiHouseAct = (HiHouse)getActivity();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_simulator, container, false);
		
		simu_containter = (LinearLayout) mRootView.findViewById(R.id.simulator_container);
		btn_accept = (Button) mRootView.findViewById(R.id.accept_btn);
		btn_cancel = (Button) mRootView.findViewById(R.id.cancel_btn);
		
		setBtnListeners();

		profiles = new ArrayList<Profile>();
		mAdapter = new ProfileListAdapter(getActivity());
		
		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.SIMULATOR_GET_PROFILES, true, "profiles/all", "token="+hiHouseAct.getUser().getToken()));
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
		
        return mRootView;
    }

	private void setBtnListeners() {
		btn_accept.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				int profId = profiles.get(selectedProfilePos).getId();
				JSONObject builder = new JSONObject();
				JSONArray devArray = new JSONArray();
				try{
					for(int i=0;i<devices.size();i++){
						CheckBox cb = (CheckBox)simu_containter.getChildAt(i);
						if(cb.isChecked()){
							Device d = devices.get(i);
							devArray.put(d.getId());
						}
					}
					builder.put("devices", devArray);
				}
				catch (JSONException e){}
				
				hiHouseAct.mHiHouseService.sendCommand(new Command(Request.SIMULATOR_UPDATE, false, "simulation/"+profId+"?token="+hiHouseAct.getUser().getToken(), builder.toString()));
				hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
			}
		});
		
		btn_cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				getActivity().getFragmentManager().popBackStack();
			}
		});
	}

	public void setProfileSpinner(String str) {
        JSONArray profArray;
		JSONObject profInfo;
		try{
			profArray = new JSONArray(str);
			for(int i=0; i<profArray.length(); i++){
    			profInfo = profArray.getJSONObject(i);
    			Profile p = new Profile(profInfo.getInt("id"), profInfo.getString("name"));
    			profiles.add(p);
    		}
		}
		catch(JSONException e){e.printStackTrace();}
        
		Spinner spinner = (Spinner) mRootView.findViewById(R.id.profile_spinner);
		
		// Apply the adapter to the spinner
		spinner.setAdapter(mAdapter);
		spinner.setOnItemSelectedListener(this);
		
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // profiles.get(pos).getId()
		selectedProfilePos = pos;
		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.SIMULATOR_GET_DEVICES, true, "profiles/"+profiles.get(pos).getId()+"/devices", "token="+hiHouseAct.getUser().getToken()));
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
    }
	
	public void loadDevices(String str){
		simu_containter.removeAllViews();
		
		devices = new ArrayList<Device>();
		JSONArray devArray;
		JSONObject deviceInfo;
    	try{
    		devArray = new JSONArray(str);
    		for(int i=0; i<devArray.length(); i++){
    			deviceInfo = devArray.getJSONObject(i);
    			Device d = new Device(deviceInfo.getInt("id"), deviceInfo.getString("name"));
    			d.setState(false);
    			devices.add(d);
    			CheckBox check = new CheckBox(this.getActivity());
    			check.setText(d.getName());
    			check.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    			simu_containter.addView(check);
    		}
    	}
    	catch(JSONException e){
    		e.printStackTrace();
    	}
	}
	
	public void updateSimulatorResult(boolean updated) {
		if(!updated) {
			Toast.makeText(getActivity(), "El Simulador no pudo ser modificado.", Toast.LENGTH_LONG).show();
			return;
		}
		Toast.makeText(getActivity(), "Simulador modificado exitosamente.", Toast.LENGTH_LONG).show();
		getActivity().getFragmentManager().popBackStack();
		return;
	}

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    //adapter for list
  	private class ProfileListAdapter extends ArrayAdapter<Profile> {
  		private Context mContext;
  		
  		public ProfileListAdapter(Context context) {
  			super(context, R.layout.simple_row, profiles);
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
  			tv.setText(profiles.get(position).getName());
  			return rowView;
  		}
  		
  		@Override
  		public int getCount(){
  			return profiles.size();
  		}
  	}
}
