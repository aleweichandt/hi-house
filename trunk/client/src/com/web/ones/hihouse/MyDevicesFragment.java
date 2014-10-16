package com.web.ones.hihouse;

import org.json.JSONException;
import org.json.JSONObject;

import com.web.ones.hihouse.UserAlarmDestDialog.OnAlarmDestListener;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MyDevicesFragment extends Fragment implements OnAlarmDestListener {

	private View mRootView;
	private ImageButton btn_start_stop, btn_alarm;
	private TextView txt_select;
	private ExpandableListView expListView;
	private ExpandableListAdapter myExpListAdapter;
	private TextView temp_txt;
	private SeekBar temp_seekBar;
	private ProgressBar loading_bar_temp, loading_bar_simu, loading_bar_alarm;
	private int seekBar_temp_corrector = 14;
	private int actual_temp = -1;
	private HiHouse hiHouseAct;
	private boolean simuState = false, alarmState = false;
	
	public MyDevicesFragment() {
        // Empty constructor required for fragment subclasses
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		hiHouseAct = (HiHouse)getActivity();
		
		myExpListAdapter = new MyExpandableListAdapter(hiHouseAct);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_my_devices, container, false);
		
		expListView = (ExpandableListView) mRootView.findViewById(R.id.profiles_explist);
        
		btn_start_stop = (ImageButton) mRootView.findViewById(R.id.start_stop_button);
		btn_alarm = (ImageButton) mRootView.findViewById(R.id.alarm_button);
		txt_select = (TextView) mRootView.findViewById(R.id.select_item_text);
		
		temp_txt = (TextView) mRootView.findViewById(R.id.temp_txt);
		temp_seekBar = (SeekBar) mRootView.findViewById(R.id.seekBar);
		loading_bar_temp = (ProgressBar) mRootView.findViewById(R.id.temp_loading_bar);
		loading_bar_simu = (ProgressBar) mRootView.findViewById(R.id.simu_loading_bar);
		loading_bar_alarm = (ProgressBar) mRootView.findViewById(R.id.alarm_loading_bar);
		
		setEventsListeners();
        
        // setting list adapter
        expListView.setAdapter(myExpListAdapter);

        hiHouseAct.mHiHouseService.sendCommand(new Command(Request.GET_USER_DEVICES, true, "users/"+hiHouseAct.getUser().getId()+"/devices?token="+hiHouseAct.getUser().getToken()+"&add_voice_id=true&add_state=true", ""));
        hiHouseAct.mHiHouseService.sendCommand(new Command(Request.GET_DESIRED_TEMP, true, "temperature", "token="+hiHouseAct.getUser().getToken()));
        hiHouseAct.mHiHouseService.sendCommand(new Command(Request.ALARM_STATE, true, "security/state", "token="+hiHouseAct.getUser().getToken()));
        
        return mRootView;
    }
	
	@Override
	public void onResume(){
        super.onResume();
	}
	
	@Override 
	public void onPause(){
		super.onPause();
	}
	
	private void setEventsListeners() {		
		btn_start_stop.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				btn_start_stop.setVisibility(View.GONE);
        		loading_bar_simu.setVisibility(View.VISIBLE);
        		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.SIMULATOR_STATE, false, "simulation/"+((MyExpandableListAdapter) myExpListAdapter).getSelectedProf()+"/state?token="+hiHouseAct.getUser().getToken()+"&enabled="+!simuState, ""));
			}
		});
		
		btn_alarm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				btn_alarm.setVisibility(View.GONE);
        		loading_bar_alarm.setVisibility(View.VISIBLE);
        		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.ALARM_STATE, false, "security/state?token="+hiHouseAct.getUser().getToken(), ""+!alarmState));
			}
		});
		
		btn_alarm.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				UserAlarmDestDialog ud = new UserAlarmDestDialog();
				ud.setTargetFragment(MyDevicesFragment.this, 0);
        		ud.show(getFragmentManager(), UserAlarmDestDialog.class.getName());
				return true;
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
					loading_bar_temp.setVisibility(View.VISIBLE);
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
            	String request = "devices/" + dvc.getId() + "/state?token=" + tkn + "&enabled=";

            	hiHouseAct.mHiHouseService.sendCommand(new Command(Request.SET_DEVICE_STATE, false, request+!dvc.getState(),""));
	    		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
	    		
            	return true;
            }
        });
		
		expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPos, long id) {
				User usr = hiHouseAct.getUser();
        		Profile prf = usr.getProfiles().get(groupPos);
        		((MyExpandableListAdapter) myExpListAdapter).setSelectedProf(prf.getId());
        		txt_select.setText("Simulador Perfil \""+prf.getName()+"\":");
        		btn_start_stop.setVisibility(View.GONE);
        		loading_bar_simu.setVisibility(View.VISIBLE);
        		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.SIMULATOR_STATE, true, "simulation/"+prf.getId()+"/state", "token="+hiHouseAct.getUser().getToken()));
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
		loading_bar_temp.setVisibility(View.GONE);
		temp_txt.setVisibility(View.VISIBLE);
	}

	public void setLastTemp(boolean set) {
		if(!set){
			temp_seekBar.setProgress(actual_temp);
			Toast.makeText(getActivity(), "No se pudo actualizar la temperatura.", Toast.LENGTH_SHORT).show();
		}
		loading_bar_temp.setVisibility(View.GONE);
	}
	
	public void updateSimulatorState(String str){
		loading_bar_simu.setVisibility(View.GONE);
		if(str.equals("")){
			txt_select.setText("Seleccione nuevamente un perfil");
			Toast.makeText(getActivity(), "No se pudo obtener el estado del Simulador.", Toast.LENGTH_SHORT).show();
			return;
		}
		try{
			JSONObject reader = new JSONObject(str);
			simuState = reader.getBoolean("state");
			btn_start_stop.setImageResource(simuState?R.drawable.ic_action_pause_over_video:R.drawable.ic_action_play_over_video);
			btn_start_stop.setVisibility(View.VISIBLE);
		}
		catch(JSONException e){e.printStackTrace();}
	}
	
	public void updateAlarmState(String str){
		loading_bar_alarm.setVisibility(View.GONE);
		if(str.equals("")){
			Toast.makeText(getActivity(), "No se pudo obtener el estado de la Alarma.", Toast.LENGTH_SHORT).show();
			return;
		}
		try{
			JSONObject reader = new JSONObject(str);
			alarmState = reader.getBoolean("state");
			btn_alarm.setImageResource(alarmState?R.drawable.ic_action_secure:R.drawable.ic_action_not_secure);
			btn_alarm.setVisibility(View.VISIBLE);
		}
		catch(JSONException e){e.printStackTrace();}
	}
	
	@Override
	public void OnAlarmDestConfirm(int userid){
		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.ALARM_DEST, false, "security/alarm_conf?token="+hiHouseAct.getUser().getToken(),""+userid));
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
	}
	
	public void updateAlarmDest(boolean set){
		if(set) Toast.makeText(getActivity(), "Destinatario de alarma modificado.", Toast.LENGTH_SHORT).show();
		else Toast.makeText(getActivity(), "No se puedo modificar el destinatario de alarma.", Toast.LENGTH_SHORT).show();;
	}
}
