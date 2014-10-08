package com.web.ones.hihouse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceAdminFragment extends ListFragment implements OnItemClickListener{
	
	private DeviceInfoFragment mDeviceFragment = null;
	private View mRootView;
	private ListView mList = null;
	private HiHouse hiHouseAct;
	private DeviceListAdapter mAdapter;
	private ArrayList<Device> devices;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hiHouseAct = (HiHouse)getActivity();
		devices = new ArrayList<Device>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_devices, container, false);
		
		mList = (ListView) mRootView.findViewById(android.R.id.list);
		
		//Cargamos lista vacia mientras vuelve el request
		mAdapter = new DeviceListAdapter(getActivity());
		mList.setAdapter(mAdapter);
		
		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.GET_ALL_DEVICES, true, "devices/all", "token="+hiHouseAct.getUser().getToken()));
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
		
		return mRootView;
	}
	
	public void loadDevicesList(String str) {
		JSONArray devArray;
		JSONObject deviceInfo;
    	try{
    		devArray = new JSONArray(str);
    		for(int i=0; i<devArray.length(); i++){
    			deviceInfo = devArray.getJSONObject(i);
    			Device d = new Device(deviceInfo.getInt("id"), deviceInfo.getString("name"));
    			devices.add(d);
    		}
    	}
    	catch(JSONException e){e.printStackTrace();}
    	
    	mAdapter.notifyDataSetChanged();
    	hiHouseAct.setLoadingBarVisibility(View.GONE);
	}
	
	public void refreshDevices() {
		devices = new ArrayList<Device>();
		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.GET_ALL_DEVICES, true, "devices/all", "token="+hiHouseAct.getUser().getToken()));
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mList.setOnItemClickListener(null);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
		Device d = devices.get(pos);
		mDeviceFragment = new DeviceInfoFragment();
		Bundle args = new Bundle();
		args.putBoolean(DeviceInfoFragment.ARG_IS_ADD, false);
		args.putString(DeviceInfoFragment.ARG_DEVICE_NAME, d.getName());
		args.putInt(DeviceInfoFragment.ARG_DEVICE_ID, d.getId());
		mDeviceFragment.setArguments(args);
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		ft.add(R.id.userinfo_container, mDeviceFragment, DeviceInfoFragment.class.getName());
		ft.addToBackStack(DeviceInfoFragment.class.toString());
		ft.commit();
	}
	
	//adapter for list
	private class DeviceListAdapter extends ArrayAdapter<Device> {
		private Context mContext;
		
		public DeviceListAdapter(Context context) {
			super(context, R.layout.simple_row, devices);
			mContext = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.simple_row, parent, false);
			TextView tv = (TextView)rowView.findViewById(R.id.row_name);
			tv.setText(devices.get(position).getName());
			return rowView;
		}
		
		@Override
		public int getCount(){
			return devices.size();
		}
	}
}
