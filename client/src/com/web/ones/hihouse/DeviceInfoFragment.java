package com.web.ones.hihouse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.web.ones.hihouse.PickerDialog.OnPickerDialogListener;

import android.os.Bundle;
import android.app.Fragment;
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
	final static String ARG_DEVICE_TYPE = "type";
	final static String ARG_DEVICE_PIN1 = "pin1";
	final static String ARG_DEVICE_PIN2 = "pin2";
	final static String ARG_DEVICE_PIN3 = "pin3";
	//TODO add subtype para actuador termico
	final static String ARG_IS_ADD = "isAddOperation";
	private static final int DEVICE_PIN_MIN_VALUE = 1;
	private static final int DEVICE_PIN_MAX_VALUE = 64;
	
	private boolean mIsAddOperation = false;
	private boolean mHasSubType = false;
	private boolean mState = false;
	private String mName;
	private String id;
	private View mMainView;
	private TextView mPinTextView;
	private CheckBox mPinCheckView;
	private HiHouse hiHouseAct;
	private int type;
	private ArrayList<Integer> pinList;
	private EditText device_name;
	private Spinner typeSpinner, subtypeSpinner;
	private int actualPin;

	public DeviceInfoFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		hiHouseAct = (HiHouse)getActivity();
		pinList = new ArrayList<Integer>();
		
		Bundle args = getArguments();
		if (args != null){
			mName = args.getString(ARG_DEVICE_NAME, "Nuevo");
			id = args.getString(ARG_DEVICE_ID, "");
			type = args.getInt(ARG_DEVICE_TYPE, -1);
			for(int i=1; i<=3; i++){
				pinList.add(args.getInt("pin"+i, -1));
			}
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
		if(!mIsAddOperation){
			typeSpinner.setSelection(type);
			for(int i=1; i<=3; i++){
				if(pinList.get(i-1)>-1){
					int resId = getResources().getIdentifier("deviceinfo_pin"+i+"_enable", "id", hiHouseAct.getPackageName());
					((CheckBox)mMainView.findViewById(resId)).setChecked(true);
					resId = getResources().getIdentifier("deviceinfo_pin"+i+"_value", "id", hiHouseAct.getPackageName());
					((TextView)mMainView.findViewById(resId)).setText(""+pinList.get(i-1));
				}
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
		JSONObject builder = new JSONObject();
		JSONArray devArray = new JSONArray();
		try{
			builder.put("name", deviceName);
			builder.put("type", type);
			builder.put("voice_id", deviceName);//TODO voice id el nombre??
			for(int i=1; i<=3; i++){
				builder.put("pin"+i, pinList.get(i-1));
			}
			if(type==Device.DEVICE_TYPE_AC_TERMAL) builder.put("subtype", subtypeSpinner.getSelectedItemPosition());//TODO como son los sub-tipos?
		}
		catch (JSONException e){}
		
		//TODO revisar ID (puse el name por ahora)
		if(mIsAddOperation) hiHouseAct.mHiHouseService.sendCommand(new Command(Request.ADD_DEVICE, false, "devices/"+deviceName.toLowerCase().replace(" ", "")+"?token="+hiHouseAct.getUser().getToken(), builder.toString()));
		else hiHouseAct.mHiHouseService.sendCommand(new Command(Request.UPDATE_PROFILE, false, "profiles/"+id+"/update?token="+hiHouseAct.getUser().getToken(), builder.toString()));
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
	}
	
	private void onCancelPressed() {
		//TODO rollback changes
		if(mIsAddOperation) {
			getActivity().getFragmentManager().popBackStack();
			return;
		}
		setEditMode(false);
	}
	
	private void onDeletePressed() {
		//TODO ask before
		//TODO remove user
		getActivity().getFragmentManager().popBackStack();
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
		if(mHasSubType) {
			//do without check as we only have one subtype
			subtypeSpinner = (Spinner) mMainView.findViewById(R.id.deviceinfo_subtype);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
			        R.array.device_term_subtype_items, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			subtypeSpinner.setAdapter(adapter);
		}
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

}
