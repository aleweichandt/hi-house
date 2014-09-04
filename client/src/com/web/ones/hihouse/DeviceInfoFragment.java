package com.web.ones.hihouse;

import java.util.ArrayList;

import com.web.ones.hihouse.PickerDialog.OnPickerDialogListener;

import android.os.Bundle;
import android.app.Fragment;
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

public class DeviceInfoFragment extends Fragment implements
OnClickListener,
OnItemSelectedListener,
OnPickerDialogListener{
	private static final int DEVICE_PIN_MIN_VALUE = 1;
	private static final int DEVICE_PIN_MAX_VALUE = 64;
	private static final int DEVICE_TYPE_TERMAL_ACTUATOR = 2;
	
	private boolean mIsAddOperation = false;
	private boolean mHasSubType = false;
	private boolean mState = false;
	private String mName;
	private View mMainView;
	private TextView mPinView;

	public DeviceInfoFragment(String name, boolean isAddOperation) {
		mName = name;
		mIsAddOperation = isAddOperation;
		mState = mIsAddOperation;
		mHasSubType = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mMainView = inflater.inflate(R.layout.fragment_device_info, container, false);
		loadDeviceInfo();
		setEditMode(false);
		return mMainView;
	}
	
	private void loadDeviceInfo() {
		((EditText)mMainView.findViewById(R.id.deviceinfo_name)).setText(mName);
		Spinner spinner = (Spinner) mMainView.findViewById(R.id.deviceinfo_type);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.device_type_items, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
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
		//TODO save changes
		if(mIsAddOperation) {
			getActivity().getFragmentManager().popBackStack();
			return;
		}
		setEditMode(false);
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
		if(viewid == R.id.deviceinfo_pin2_enable) pinValueId = R.id.deviceinfo_pin2_value;
		if(viewid == R.id.deviceinfo_pin3_enable) pinValueId = R.id.deviceinfo_pin3_value;
		mPinView = (TextView)mMainView.findViewById(pinValueId);
		
		if(enabled) {
			Bundle b = new Bundle();
			b.putInt(PickerDialog.PICKER_MIN_VALUE, DEVICE_PIN_MIN_VALUE);
			b.putInt(PickerDialog.PICKER_MAX_VALUE, DEVICE_PIN_MAX_VALUE);
			
			if(mPinView.getText().length() > 0) {
				int value = Integer.parseInt(mPinView.getText().toString());
				b.putInt(PickerDialog.PICKER_CURRENT_VALUE, value);
			}
			PickerDialog md = new PickerDialog(this, b);
			md.show(getActivity().getFragmentManager(), "pin");
		} else {
			mPinView.setVisibility(View.GONE);
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
		mHasSubType = (pos == DEVICE_TYPE_TERMAL_ACTUATOR);
		if(mHasSubType) {
			//do without check as we only have one subtype
			Spinner spinner = (Spinner) mMainView.findViewById(R.id.deviceinfo_subtype);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
			        R.array.device_term_subtype_items, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
		}
		setEditMode(mState);//update to show/hide subtype
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}

	@Override
	public void OnPickerConfirm(int value) {
		mPinView.setText(Integer.toString(value));
		mPinView.setVisibility(View.VISIBLE);
	}

	@Override
	public void OnPickerCancel(int value) {
		// TODO uncheck checkbox
		
	}

}
