package com.web.ones.hihouse;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MyDevicesFragment extends Fragment {

	private View mRootView;
	private ListView mList = null;
	private ArrayAdapter<String> mAdapter;
	private Switch onOffSwitch;
	
	public MyDevicesFragment() {
        // Empty constructor required for fragment subclasses
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_my_devices, container, false);
        
		onOffSwitch = (Switch) mRootView.findViewById(R.id.on_off_switch);
		onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		            // The toggle is enabled
		        } else {
		            // The toggle is disabled
		        }
		        Toast.makeText(getActivity(), "Monitored switch is " + (isChecked ? "on" : "off"),Toast.LENGTH_SHORT).show();
		    }
		});
		
        loadDevices();
        
        return mRootView;
    }
	
	private void loadDevices() {
		//TODO load real devices
		String[] values = new String[] { "Alarma Central", "Climatizador Living", "Luz Cocina", "Luz Puerta Principal", "Porton Garage" };

	    final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < values.length; ++i) {
	      list.add(values[i]);
	    }
	    mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.device_item, list);
	    
	    mList = (ListView) mRootView.findViewById(R.id.my_devices_list);
	    mList.setAdapter(mAdapter);
	    //mList.setOnItemClickListener(this);
	}
	
}
