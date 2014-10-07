package com.web.ones.hihouse;

import java.util.ArrayList;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DeviceAdminFragment extends ListFragment implements 
	OnItemClickListener{
	
	private DeviceInfoFragment mDeviceFragment = null;
	private View mRootView;
	private ListView mList = null;
	private ArrayAdapter<String> mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_devices, container, false);
		loadDevicesList();
		
		return mRootView;
	}
	
	private void loadDevicesList() {
		//TODO load real users
		String[] values = new String[] { "Luz Cocina", "Luz baño", "AC habitacion1",
		        "Puerta Cochera", "Sensor Luminico 1", "Sensor Termico 1" };

	    final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < values.length; ++i) {
	      list.add(values[i]);
	    }
	    mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_row, list);
	    
	    mList = (ListView) mRootView.findViewById(android.R.id.list);
	    mList.setAdapter(mAdapter);
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
		String name = mAdapter.getItem(pos);
		Bundle args = new Bundle();
		args.putBoolean(DeviceInfoFragment.ARG_IS_ADD, false);
		args.putString(DeviceInfoFragment.ARG_DEVICE_NAME, "test");
		args.putString(DeviceInfoFragment.ARG_DEVICE_ID, "test");
		args.putInt(DeviceInfoFragment.ARG_DEVICE_TYPE, 3);
		args.putInt("pin1", 5);
		args.putInt("pin3", 12);
		mDeviceFragment = new DeviceInfoFragment();
		mDeviceFragment.setArguments(args);
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		ft.add(R.id.userinfo_container, mDeviceFragment, DeviceInfoFragment.class.getName());
		ft.addToBackStack(DeviceInfoFragment.class.toString());
		ft.commit();
	}
}
