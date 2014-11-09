package com.web.ones.hihouse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.web.ones.hihouse.PickerDialog.OnPickerDialogListener;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceInfoFragment extends Fragment implements
OnClickListener,
OnItemSelectedListener,
OnPickerDialogListener{
	final static String ARG_DEVICE_NAME = "name";
	final static String ARG_DEVICE_ID = "id";
	final static String ARG_IS_ADD = "isAddOperation";
	private static final int DEVICE_PIN_MIN_VALUE = 1;
	private static final int DEVICE_PIN_MAX_VALUE = 64;
	
	private boolean mIsAddOperation = false;
	private boolean mHasSubType = false;
	private boolean mState = false;
	private String mName;
	private int id;
	private View mMainView;
	private TextView mPinTextView;
	private CheckBox mPinCheckView;
	private HiHouse hiHouseAct;
	private int type, subtype;
	private ArrayList<Integer> pinList;
	private EditText device_name, device_voice_command;
	private Spinner typeSpinner, subtypeSpinner;
	private int actualPin;

	public DeviceInfoFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		hiHouseAct = (HiHouse)getActivity();
		pinList = new ArrayList<Integer>();
		
		//inicializamos la lista de pines
		pinList.add(-1);
		pinList.add(-1);
		pinList.add(-1);
		
		Bundle args = getArguments();
		if (args != null){
			mName = args.getString(ARG_DEVICE_NAME, "Nuevo");
			id = args.getInt(ARG_DEVICE_ID, -1);
			mIsAddOperation = args.getBoolean(ARG_IS_ADD);
			mState = mIsAddOperation;
			mHasSubType = false;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mMainView = inflater.inflate(R.layout.fragment_device_info, container, false);
		
		device_name = (EditText)mMainView.findViewById(R.id.deviceinfo_name);
		device_name.setText(mName);
		typeSpinner = (Spinner) mMainView.findViewById(R.id.deviceinfo_type);
		subtypeSpinner = (Spinner) mMainView.findViewById(R.id.deviceinfo_subtype);
		device_voice_command = (EditText)mMainView.findViewById(R.id.deviceinfo_voice_command);
		
		loadDeviceInfo();
		setEditMode(mIsAddOperation || mState);
		return mMainView;
	}
	
	private void loadDeviceInfo() {
		((EditText)mMainView.findViewById(R.id.deviceinfo_name)).setText(mName);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.device_type_items, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinner.setAdapter(adapter);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
		        R.array.device_term_subtype_items, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		subtypeSpinner.setAdapter(adapter2);
		if(!mIsAddOperation){
			hiHouseAct.mHiHouseService.sendCommand(new Command(Request.GET_DEVICE, true, "devices/"+id, "token="+hiHouseAct.getUser().getToken()));
			hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
		}
	}
	
	public void updateDeviceInfo(String str){
		JSONObject deviceInfo;
		try{
			deviceInfo = new JSONObject(str);
			type = deviceInfo.getInt("type");
			device_voice_command.setText(deviceInfo.getString("voice_id"));
			for(int i=1; i<=3; i++){
				pinList.set(i-1, deviceInfo.getInt("pin"+i));
			}
			if(type==Device.DEVICE_TYPE_AC_TERMAL) subtype = Integer.parseInt(deviceInfo.getString("subtype"));
		}
		catch(JSONException e){e.printStackTrace();}
		
		typeSpinner.setSelection(type);
		if(type==Device.DEVICE_TYPE_AC_TERMAL) subtypeSpinner.setSelection(subtype-1);
		for(int i=1; i<=3; i++){
			if(pinList.get(i-1)>-1){
				int resId = getResources().getIdentifier("deviceinfo_pin"+i+"_enable", "id", hiHouseAct.getPackageName());
				((CheckBox)mMainView.findViewById(resId)).setChecked(true);
				resId = getResources().getIdentifier("deviceinfo_pin"+i+"_value", "id", hiHouseAct.getPackageName());
				((TextView)mMainView.findViewById(resId)).setText(""+pinList.get(i-1));
			}
		}
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
		Spinner s =(Spinner) mMainView.findViewById(R.id.deviceinfo_type);
		s.setOnItemSelectedListener((OnItemSelectedListener)listener);
		mMainView.findViewById(R.id.deviceinfo_confirm).setOnClickListener(listener);
		mMainView.findViewById(R.id.deviceinfo_cancel).setOnClickListener(listener);
		mMainView.findViewById(R.id.deviceinfo_edit).setOnClickListener(listener);
		mMainView.findViewById(R.id.deviceinfo_delete).setOnClickListener(listener);
		mMainView.findViewById(R.id.deviceinfo_pin1_enable).setOnClickListener(listener);
		mMainView.findViewById(R.id.deviceinfo_pin2_enable).setOnClickListener(listener);
		mMainView.findViewById(R.id.deviceinfo_pin3_enable).setOnClickListener(listener);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case (R.id.deviceinfo_confirm):
			onConfirmPressed();
			break;
		case (R.id.deviceinfo_cancel):
			onCancelPressed();
			break;
		case (R.id.deviceinfo_edit):
			onEditPressed();
			break;
		case (R.id.deviceinfo_delete):
			onDeletePressed();
			break;
		case (R.id.deviceinfo_pin1_enable):
		case (R.id.deviceinfo_pin2_enable):
		case (R.id.deviceinfo_pin3_enable):
			CheckBox cb = (CheckBox)v;
			onPinPressed(v.getId(), cb.isChecked());
		default:
			break;
		}
		
	}

	private void onEditPressed() {
		setEditMode(true);
	}
	
	private void onConfirmPressed() {
		String deviceName = device_name.getText().toString();
		String deviceVoiceCommand = device_voice_command.getText().toString();
		JSONObject builder = new JSONObject();
		JSONArray devArray = new JSONArray();
		
		Boolean validate = validateData(deviceName);
		if(validate)
		{
			return;
		}
		try{
			builder.put("name", deviceName);
			builder.put("type", type);
			builder.put("voice_id", deviceVoiceCommand.toLowerCase());
			for(int i=1; i<=3; i++){
				builder.put("pin"+i, pinList.get(i-1));
			}
			if(type==Device.DEVICE_TYPE_AC_TERMAL) builder.put("subtype", Integer.toString(subtypeSpinner.getSelectedItemPosition()+1));
		}
		catch (JSONException e){}
		
		if(mIsAddOperation) hiHouseAct.mHiHouseService.sendCommand(new Command(Request.ADD_DEVICE, false, "devices/add?token="+hiHouseAct.getUser().getToken(), builder.toString()));
		else hiHouseAct.mHiHouseService.sendCommand(new Command(Request.UPDATE_DEVICE, false, "devices/"+id+"/update?token="+hiHouseAct.getUser().getToken(), builder.toString()));
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
	}
	
	private Boolean validateData(String deviceName) {
		ArrayList<String> errors = new ArrayList<String>();
		Boolean state = false;
		if(deviceName.isEmpty())
		{
			errors.add("Ingrese un nombre");
			state = true;
		}
		
		Boolean validatePin = false;
		for(int i=1; i<=3; i++){	
			if(pinList.get(i-1) != -1)
			{
				validatePin = true;
			}
		}
		
		if(!validatePin)
		{
			errors.add("Ingrese al menos un pin");
			state = true;
		}
		
		String cadena = "";
		if(state)
		{
			for (String a : errors)
			{
				cadena += a + '\n';
			}
			
			Toast.makeText(getActivity(), cadena.substring(0, cadena.length() - 1), Toast.LENGTH_SHORT).show();
		}
		return state;
	}
	
	private void onCancelPressed() {
		if(mIsAddOperation) {
			getActivity().getFragmentManager().popBackStack();
			return;
		}
		setEditMode(false);
	}
	
	private void onDeletePressed() {
		confirmDelete();
	}
	
	private void onPinPressed(int viewid, boolean enabled) {
		int pinValueId = R.id.deviceinfo_pin1_value;
		actualPin = 0;
		if(viewid == R.id.deviceinfo_pin2_enable) {pinValueId = R.id.deviceinfo_pin2_value;actualPin=1;}
		if(viewid == R.id.deviceinfo_pin3_enable) {pinValueId = R.id.deviceinfo_pin3_value;actualPin=2;}
		mPinTextView = (TextView)mMainView.findViewById(pinValueId);
		mPinCheckView = (CheckBox)mMainView.findViewById(viewid);
		
		if(enabled) {
			Bundle b = new Bundle();
			b.putInt(PickerDialog.PICKER_MIN_VALUE, DEVICE_PIN_MIN_VALUE);
			b.putInt(PickerDialog.PICKER_MAX_VALUE, DEVICE_PIN_MAX_VALUE);
			
			if(mPinTextView.getText().length() > 0) {
				int value = Integer.parseInt(mPinTextView.getText().toString());
				b.putInt(PickerDialog.PICKER_CURRENT_VALUE, value);
			}
			PickerDialog md = new PickerDialog(this, b);
			md.show(getActivity().getFragmentManager(), "pin");
		} else {
			mPinTextView.setVisibility(View.GONE);
			pinList.set(actualPin, -1);
		}
	}
	
	private void setEditMode(boolean on) {
		mMainView.findViewById(R.id.deviceinfo_name).setEnabled(on);
		mMainView.findViewById(R.id.deviceinfo_voice_command).setEnabled(on);
		mMainView.findViewById(R.id.deviceinfo_type).setEnabled(on);
		mMainView.findViewById(R.id.deviceinfo_subtype).setEnabled(on);
		mMainView.findViewById(R.id.deviceinfo_pin1_enable).setEnabled(on);
		mMainView.findViewById(R.id.deviceinfo_pin2_enable).setEnabled(on);
		mMainView.findViewById(R.id.deviceinfo_pin3_enable).setEnabled(on);
		mMainView.findViewById(R.id.deviceinfo_subtype_frame).setVisibility(mHasSubType?View.VISIBLE:View.GONE);
		mMainView.findViewById(R.id.deviceinfo_edit).setVisibility(on?View.GONE:View.VISIBLE);
		mMainView.findViewById(R.id.deviceinfo_delete).setVisibility(on?View.GONE:View.VISIBLE);
		mMainView.findViewById(R.id.deviceinfo_confirm).setVisibility(on?View.VISIBLE:View.GONE);
		mMainView.findViewById(R.id.deviceinfo_cancel).setVisibility(on?View.VISIBLE:View.GONE);
		mState = on;
	}

	@Override
	public void onItemSelected(AdapterView<?> ad, View v, int pos,	long id) {
		type = pos;
		mHasSubType = (pos == Device.DEVICE_TYPE_AC_TERMAL);
		setEditMode(mState);//update to show/hide subtype
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}

	@Override
	public void OnPickerConfirm(int value) {
		mPinTextView.setText(Integer.toString(value));
		mPinTextView.setVisibility(View.VISIBLE);
		pinList.set(actualPin, value);
	}

	@Override
	public void OnPickerCancel(int value) {
		mPinCheckView.setChecked(false);
		pinList.set(actualPin, -1);
	}
	
	public void addDeviceResult(boolean added) {
		if(!added) {
			Toast.makeText(getActivity(), "Un dispositivo con ese nombre ya existe.", Toast.LENGTH_LONG).show();
			return;
		}
		if(mIsAddOperation) {
			Toast.makeText(getActivity(), "Dispositivo agregado exitosamente.", Toast.LENGTH_SHORT).show();
			getActivity().getFragmentManager().popBackStack();
			return;
		}
		setEditMode(false);
	}
	
	public void updateDeviceResult(boolean updated) {
		if(!updated) {
			Toast.makeText(getActivity(), "El dispositivo no pudo ser actualizado.", Toast.LENGTH_LONG).show();
			return;
		}
		Toast.makeText(getActivity(), "Dispositivo actualizado exitosamente.", Toast.LENGTH_SHORT).show();
		getActivity().getFragmentManager().popBackStack();
		return;
	}
	
	public void confirmDelete() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				 switch (which) {
				 case DialogInterface.BUTTON_POSITIVE:
					 	hiHouseAct.mHiHouseService.sendCommand(new Command(Request.DELETE_DEVICE, false, "devices/"+id+"/delete?token="+hiHouseAct.getUser().getToken(), ""));
					 	hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
				        break;
				
				 case DialogInterface.BUTTON_NEGATIVE:
				        // No button clicked do nothing
					 	//Toast.makeText(getActivity(), "No Clicked",Toast.LENGTH_LONG).show();
				        break;
				 }
			}
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("¿Está seguro que desea eliminar el dispositivo \""+device_name.getText().toString()+"\"?")
                     .setPositiveButton("Si", dialogClickListener)
                     .setNegativeButton("No", dialogClickListener).show();
	}
	
	public void deleteDeviceResult(boolean deleted) {
		if(deleted){
			Toast.makeText(getActivity(), "Dispositivo eliminado exitosamente.", Toast.LENGTH_LONG).show();
			getActivity().getFragmentManager().popBackStack();
		}
		else {
			Toast.makeText(getActivity(), "El Dispositivo no pudo ser eliminado.", Toast.LENGTH_LONG).show();
			hiHouseAct.setLoadingBarVisibility(View.GONE);
		}
		
	}

}
