package com.web.ones.hihouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
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
	private List<String> listDataHeader;
	private HashMap<String, List<String>> listDataChild;
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
		
        loadProfilesDevicesList();
        
        //myExpListAdapter = new MyExpandableListAdapter(this.getActivity(), listDataHeader, listDataChild);
        myExpListAdapter = new MyExpandableListAdapter(this.getActivity(), ((HiHouse)getActivity()).getUser().getProfiles());
        // setting list adapter
        expListView.setAdapter(myExpListAdapter);
        
        return mRootView;
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
            	String header = listDataHeader.get(groupPos);
            	String item = listDataChild.get(header).get(childPos);
            	if(item.contains("Alarma")){
            		//TODO mostrar popup destinatario alarma
            		UserAlarmDestDialog ud = new UserAlarmDestDialog(null);
            		ud.show(getFragmentManager(), "userdest");
            	} else {
            		Profile prf = ((HiHouse)getActivity()).getUser().getProfiles().get(groupPos);
	            	Device dvc = prf.getDevices().get(childPos);
	            	dvc.setEstado(!dvc.getEstado());
	            	String tkn = ((HiHouse)getActivity()).getUser().getToken();
	            	String request = "devices/" + dvc.getId() + "/state?token=" + tkn + "&enabled=" + dvc.getEstado();
	            	((HiHouse)getActivity()).mHiHouseService.sendCommand(new Command(Request.SET_DEVICE_STATE, false,request,""));
            	}
	            return false;
            }
        });
	}

	private void loadProfilesDevicesList() {
		//TODO load real devices
	    int i = 0;
	    listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        
        for(Profile p : ((HiHouse)getActivity()).getUser().getProfiles()){
        	listDataHeader.add(p.getName());
        	List<String> perfil = new ArrayList<String>();
        	for(Device d : p.getDevices()){
        		perfil.add(d.getName());
        	}
        	listDataChild.put(listDataHeader.get(i++), perfil);
        }
	}
	
}