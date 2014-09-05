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
	private ExpandableListAdapter listAdapter;
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
        
        listAdapter = new MyExpandableListAdapter(this.getActivity(), listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);
        
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
            	}
                return false;
            }
        });
	}

	private void loadProfilesDevicesList() {
		//TODO load real devices
	    
	    listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        
        // Adding headers data
        listDataHeader.add("Exterior");
        listDataHeader.add("Cocina");
        listDataHeader.add("Living");
        listDataHeader.add("Alarmas");
        
        // Adding child data
        List<String> exterior = new ArrayList<String>();
        exterior.add("Luz Puerta Principal");
        exterior.add("Porton Garage");
        exterior.add("Puerta Principal");
        exterior.add("Luz Jardin Trasero");
        
        List<String> cocina = new ArrayList<String>();
        cocina.add("Luz Cocina");
        cocina.add("Persiana Cocina");
        
        List<String> living = new ArrayList<String>();
        living.add("Luz Principal Living");
        living.add("Luz Fondo Living");
        living.add("Climatizador Living");
        
        List<String> alarmas = new ArrayList<String>();
        alarmas.add("Alarma Central");
        
        // Header, Child data
        listDataChild.put(listDataHeader.get(0), exterior);
        listDataChild.put(listDataHeader.get(1), cocina);
        listDataChild.put(listDataHeader.get(2), living);
        listDataChild.put(listDataHeader.get(3), alarmas);
        
        
	}
	
}
