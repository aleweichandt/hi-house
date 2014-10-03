package com.web.ones.hihouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.web.ones.hihouse.HiHouseService.HiHouseTask;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MyDevicesFragment extends Fragment{

	private View mRootView;
	private ListView mList = null;
	private ArrayAdapter<String> mAdapter;
	private Switch onOffSwitch;
	private ImageButton btn_open_close, btn_start_stop;
	private TextView txt_select;
	private Boolean toggle_open_close=false, toggle_start_stop=false;
	//private List<String> listDataHeader;
	//private HashMap<String, List<String>> listDataChild;
	private ExpandableListView expListView;
	private ExpandableListAdapter myExpListAdapter;
	private TextView temp_txt;
	private SeekBar temp_seekBar;
	private ProgressBar temp_loading_bar;
	private String request;
	private boolean deviceState;
	private int seekBar_temp_corrector = 14;
	private int actual_temp = -1;
	private HiHouse hiHouseAct;
	
	public MyDevicesFragment() {
        // Empty constructor required for fragment subclasses
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		hiHouseAct = (HiHouse)getActivity();
		
		//myExpListAdapter = new MyExpandableListAdapter(this.getActivity(), listDataHeader, listDataChild);
        //myExpListAdapter = new MyExpandableListAdapter(hiHouseAct, hiHouseAct.getUser().getProfiles());
		myExpListAdapter = new MyExpandableListAdapter(hiHouseAct);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_my_devices, container, false);
		
		expListView = (ExpandableListView) mRootView.findViewById(R.id.profiles_explist);
        
		onOffSwitch = (Switch) mRootView.findViewById(R.id.on_off_switch);
		btn_open_close = (ImageButton) mRootView.findViewById(R.id.open_close_button);
		btn_start_stop = (ImageButton) mRootView.findViewById(R.id.start_stop_button);
		txt_select = (TextView) mRootView.findViewById(R.id.select_item_text);
		
		temp_txt = (TextView) mRootView.findViewById(R.id.temp_txt);
		temp_seekBar = (SeekBar) mRootView.findViewById(R.id.seekBar);
		temp_loading_bar = (ProgressBar) mRootView.findViewById(R.id.temp_loading_bar);
		
		setEventsListeners();
        
        // setting list adapter
        expListView.setAdapter(myExpListAdapter);

        hiHouseAct.mHiHouseService.sendCommand(new Command(Request.GET_USER_DEVICES, true, "users/admin/devices?token="+hiHouseAct.getUser().getToken()+"&add_voice_id=true&add_state=true", ""));
        //mHiHouseService.testMethod();
        
        return mRootView;
    }
	
	@Override
	public void onResume(){
		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.GET_DESIRED_TEMP, true, "temperature", "token="+hiHouseAct.getUser().getToken()));
        super.onResume();
	}
	
	@Override 
	public void onPause(){
		super.onPause();
	}
	
	private void setEventsListeners() {
		btn_open_close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				deviceState=!deviceState;
				btn_open_close.setImageResource(deviceState?R.drawable.ic_action_not_secure:R.drawable.ic_action_secure);
				hiHouseAct.mHiHouseService.sendCommand(new Command(Request.SET_DEVICE_STATE, false,request+deviceState,""));
				hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
			}
		});
		
		btn_start_stop.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				btn_start_stop.setImageResource(toggle_start_stop?R.drawable.ic_action_play_over_video:R.drawable.ic_action_pause_over_video);
				toggle_start_stop=!toggle_start_stop;
				//TODO arrancar/detener simulador
			}
		});
		
		onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		    	if(deviceState!=isChecked){
		    		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.SET_DEVICE_STATE, false,request+isChecked,""));
		    		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
			    	deviceState=!deviceState;
		    	}
		    }
		});
		
		temp_seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	
				@Override
				public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
					//suponemos una temp entre 15 y 30 grados.Si es 0 esta off.
					if(progresValue==0) temp_txt.setText("Off");
					else temp_txt.setText(""+(progresValue+seekBar_temp_corrector)+" °C");
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// Do something here, if you want to do anything at the start of touching the seekbar
					actual_temp = seekBar.getProgress();
				}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// Display the value in textview
					temp_loading_bar.setVisibility(View.VISIBLE);
					int temp = seekBar.getProgress();
					if(temp>0) temp += seekBar_temp_corrector;
					else temp = -1;
					
					hiHouseAct.mHiHouseService.sendCommand(new Command(Request.SET_DESIRED_TEMP, false, "temperature?token="+hiHouseAct.getUser().getToken(), ""+temp));
				}
		});

		expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
            @Override
            public boolean onChildClick(ExpandableListView expList, View v, int groupPos, int childPos, long id)
            {
            	User usr = hiHouseAct.getUser();
        		Profile prf = usr.getProfiles().get(groupPos);
            	Device dvc = prf.getDevices().get(childPos);
            	String tkn = usr.getToken();
            	request = "devices/" + dvc.getId() + "/state?token=" + tkn + "&enabled=";
            	
            	((MyExpandableListAdapter) myExpListAdapter).setSelectedItem(dvc.getId(), true);
            	txt_select.setVisibility(View.GONE);
            	deviceState = dvc.getState();
            	switch(dvc.getType()){
            	case Device.DEVICE_TYPE_AC_LIGHT:
            		onOffSwitch.setChecked(deviceState);
            		onOffSwitch.setVisibility(View.VISIBLE);
            		btn_open_close.setVisibility(View.GONE);
            		btn_start_stop.setVisibility(View.GONE);
            		break;
            	case Device.DEVICE_TYPE_AC_TERMAL:
            		onOffSwitch.setChecked(deviceState);
            		onOffSwitch.setVisibility(View.VISIBLE);
            		btn_open_close.setVisibility(View.GONE);
            		btn_start_stop.setVisibility(View.GONE);
            		break;
            	case Device.DEVICE_TYPE_SN_TERMAL:
            		onOffSwitch.setChecked(deviceState);
            		onOffSwitch.setVisibility(View.VISIBLE);
            		btn_open_close.setVisibility(View.GONE);
            		btn_start_stop.setVisibility(View.GONE);
            		break;
            	case Device.DEVICE_TYPE_AC_DOOR:
            		btn_open_close.setImageResource(deviceState?R.drawable.ic_action_not_secure:R.drawable.ic_action_secure);
            		onOffSwitch.setVisibility(View.GONE);
            		btn_open_close.setVisibility(View.VISIBLE);
            		btn_start_stop.setVisibility(View.GONE);
            		break;
            	}
            	return true;
            }
        });
		
		expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPos, long id) {
				User usr = hiHouseAct.getUser();
        		Profile prf = usr.getProfiles().get(groupPos);
        		((MyExpandableListAdapter) myExpListAdapter).setSelectedItem(prf.getName(), false);
        		txt_select.setVisibility(View.GONE);
        		onOffSwitch.setVisibility(View.GONE);
        		btn_open_close.setVisibility(View.GONE);
        		btn_start_stop.setVisibility(View.VISIBLE);
				return false;
			}
		});
	}
	
	public void updateExpList(){
		((BaseExpandableListAdapter) myExpListAdapter).notifyDataSetChanged();
	}
	
	public void updateTemp(String jsonStr){
		if(jsonStr.equals("")){
			temp_seekBar.setProgress(1);
			temp_seekBar.setProgress(0);
		}
		else{
			try{
				JSONObject reader = new JSONObject(jsonStr);
				int temp = reader.getInt("value");
				if(temp==-1) temp_txt.setText("Off");
				else temp_seekBar.setProgress(temp-seekBar_temp_corrector);
			}
			catch(JSONException e) {
				e.printStackTrace();
			}
		}
		temp_loading_bar.setVisibility(View.GONE);
		temp_txt.setVisibility(View.VISIBLE);
	}

	public void setLastTemp(boolean set) {
		if(!set){
			temp_seekBar.setProgress(actual_temp);
			Toast.makeText(getActivity(), "No se pudo actualizar la temperatura.", Toast.LENGTH_SHORT).show();
		}
		temp_loading_bar.setVisibility(View.GONE);
	}
}
