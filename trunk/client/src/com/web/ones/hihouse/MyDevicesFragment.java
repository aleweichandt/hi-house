package com.web.ones.hihouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.web.ones.hihouse.HiHouseService.HiHouseTask;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.Toast;

public class MyDevicesFragment extends Fragment{

	private View mRootView;
	private ListView mList = null;
	private ArrayAdapter<String> mAdapter;
	private Switch onOffSwitch;
	private ImageButton btn_open_close, btn_start_stop;
	private Boolean toggle_open_close=false, toggle_start_stop=false;
	//private List<String> listDataHeader;
	//private HashMap<String, List<String>> listDataChild;
	private ExpandableListView expListView;
	private ExpandableListAdapter myExpListAdapter;
	private EditText temp_input;
	private SeekBar temp_seekBar;
	
	public MyDevicesFragment() {
        // Empty constructor required for fragment subclasses
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_my_devices, container, false);
		
		expListView = (ExpandableListView) mRootView.findViewById(R.id.profiles_explist);
        
		onOffSwitch = (Switch) mRootView.findViewById(R.id.on_off_switch);
		btn_open_close = (ImageButton) mRootView.findViewById(R.id.open_close_button);
		btn_start_stop = (ImageButton) mRootView.findViewById(R.id.start_stop_button);
		
		temp_input = (EditText) mRootView.findViewById(R.id.temp_input);
		temp_seekBar = (SeekBar) mRootView.findViewById(R.id.seekBar);
		
		setEventsListeners();
        
        //myExpListAdapter = new MyExpandableListAdapter(this.getActivity(), listDataHeader, listDataChild);
        myExpListAdapter = new MyExpandableListAdapter(this.getActivity(), ((HiHouse)getActivity()).getUser().getProfiles());
        // setting list adapter
        expListView.setAdapter(myExpListAdapter);
        
        return mRootView;
    }
	
	@Override
	public void onResume(){
        getActivity().registerReceiver(mUpdatesReceiver, new IntentFilter(HiHouseTask.UPDATE_EXP_LIST));
        super.onResume();
	}
	
	@Override 
	public void onPause(){
		getActivity().unregisterReceiver(mUpdatesReceiver);
		super.onPause();
	}
	
	private void setEventsListeners() {
		btn_open_close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				btn_open_close.setImageResource(toggle_open_close?R.drawable.ic_action_secure:R.drawable.ic_action_not_secure);
				toggle_open_close=!toggle_open_close;
			}
		});
		
		btn_start_stop.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				btn_start_stop.setImageResource(toggle_start_stop?R.drawable.ic_action_play_over_video:R.drawable.ic_action_pause_over_video);
				toggle_start_stop=!toggle_start_stop;
			}
		});
		
		onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		            // The toggle is enabled
		        } else {
		            // The toggle is disabled
		        }
		        //Toast.makeText(getActivity(), "Monitored switch is " + (isChecked ? "on" : "off"),Toast.LENGTH_SHORT).show();
		    }
		});
		
		temp_seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	
				@Override
				public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
					//suponemos una temp entre 15 y 30 grados.
					temp_input.setText(""+(progresValue+15));
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// Do something here, 
					//if you want to do anything at the start of
					// touching the seekbar
				}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// Display the value in textview
					//temp_input.setText(progress + "/" + seekBar.getMax());
				}
		});

		expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
            @Override
            public boolean onChildClick(ExpandableListView expList, View v, int groupPos, int childPos, long id)
            {
            	User usr = ((HiHouse)getActivity()).getUser();
        		Profile prf = usr.getProfiles().get(groupPos);
            	Device dvc = prf.getDevices().get(childPos);
            	String tkn = usr.getToken();
            	String request = "devices/" + dvc.getId() + "/state?token=" + tkn + "&enabled=" + !dvc.getEstado();
            	((HiHouse)getActivity()).mHiHouseService.sendCommand(new Command(Request.SET_DEVICE_STATE, false,request,""));
            	((HiHouse)getActivity()).setLoadingBarVisibility(View.VISIBLE);
            	((MyExpandableListAdapter) myExpListAdapter).setSelectedItem(dvc.getId(), true);
            	return true;
            }
        });
		
		expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPos, long id) {
				User usr = ((HiHouse)getActivity()).getUser();
        		Profile prf = usr.getProfiles().get(groupPos);
        		((MyExpandableListAdapter) myExpListAdapter).setSelectedItem(prf.getName(), false);
				return false;
			}
		});
	}
	
	private BroadcastReceiver mUpdatesReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	((BaseExpandableListAdapter) myExpListAdapter).notifyDataSetChanged();
        }
	};
	
}
