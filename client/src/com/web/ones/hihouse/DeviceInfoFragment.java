package com.web.ones.hihouse;

import java.util.ArrayList;
import java.util.List;

import com.web.ones.hihouse.MultiChoiceDialog.OnMultiChoiceDialogListener;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

public class DeviceInfoFragment extends Fragment implements
OnClickListener{
	private static final String ARG_DEVICE_NAME = "device.name";

	private String mName;
	private View mMainView;
	
	String[] values = new String[] { "Cocina", "Living", "Baño",
	        						 "Habitacion 1", "Habitacion 2", "Garage" };

	public static DeviceInfoFragment newInstance(String name) {
		DeviceInfoFragment fragment = new DeviceInfoFragment();
		Bundle args = new Bundle();
		args.putString(ARG_DEVICE_NAME, name);
		fragment.setArguments(args);
		return fragment;
	}

	public DeviceInfoFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mName = getArguments().getString(ARG_DEVICE_NAME);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mMainView = inflater.inflate(R.layout.fragment_device_info, container, false);
		loadUserInfo();
		setEditMode(false);
		return mMainView;
	}
	
	private void loadUserInfo() {
		//((EditText)mMainView.findViewById(R.id.userinfo_name)).setText(mName);
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
		//mMainView.findViewById(R.id.userinfo_confirm).setOnClickListener(listener);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case (R.id.userinfo_confirm):
			//onConfirmPressed();
			break;
		default:
			break;
		}
		
	}
	
	private void setEditMode(boolean on) {
		//mMainView.findViewById(R.id.userinfo_name).setEnabled(on);
		//mMainView.findViewById(R.id.userinfo_edit).setVisibility(on?View.GONE:View.VISIBLE);
	}

}
