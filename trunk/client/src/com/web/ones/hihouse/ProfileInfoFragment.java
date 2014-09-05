package com.web.ones.hihouse;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ProfileInfoFragment extends Fragment implements
OnClickListener,
OnItemClickListener{
	private boolean mIsAddOperation = false;
	private boolean mState = false;
	private String mName;
	private View mMainView;

	public ProfileInfoFragment(String name, boolean isAddOperation) {
		mName = name;
		mIsAddOperation = isAddOperation;
		mState = mIsAddOperation;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mMainView = inflater.inflate(R.layout.fragment_profile_info, container, false);
		loadProfileInfo();
		setEditMode(mIsAddOperation || mState);
		return mMainView;
	}
	
	private void loadProfileInfo() {
		
		((EditText)mMainView.findViewById(R.id.profileinfo_name)).setText(mName);
		ListView lv = (ListView) mMainView.findViewById(R.id.profileinfo_devices);
		final ArrayList<String> list = getDevices();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
		        R.layout.user_row, list);
		lv.setAdapter(adapter);
	}
	
	private ArrayList<String> getDevices() {
		//TODO get device list
		String[] values = new String[] { "Luz cocina 1", "Luz cocina 2", "Puerta Cochera",
				"A/C Habitacion 1", "Luz Living" };

		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < values.length; ++i) {
			list.add(values[i]);
		}
		return list;
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
		mMainView.findViewById(R.id.profileinfo_confirm).setOnClickListener(listener);
		mMainView.findViewById(R.id.profileinfo_cancel).setOnClickListener(listener);
		mMainView.findViewById(R.id.profileinfo_edit).setOnClickListener(listener);
		mMainView.findViewById(R.id.profileinfo_delete).setOnClickListener(listener);
		ListView lv = (ListView)mMainView.findViewById(R.id.profileinfo_devices);
		lv.setOnItemClickListener((OnItemClickListener) listener);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case (R.id.profileinfo_confirm):
			onConfirmPressed();
			break;
		case (R.id.profileinfo_cancel):
			onCancelPressed();
			break;
		case (R.id.profileinfo_edit):
			onEditPressed();
			break;
		case (R.id.profileinfo_delete):
			onDeletePressed();
			break;
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
	
	private void setEditMode(boolean on) {
		mMainView.findViewById(R.id.profileinfo_name).setEnabled(on);
		mMainView.findViewById(R.id.profileinfo_edit).setVisibility(on?View.GONE:View.VISIBLE);
		mMainView.findViewById(R.id.profileinfo_delete).setVisibility(on?View.GONE:View.VISIBLE);
		mMainView.findViewById(R.id.profileinfo_confirm).setVisibility(on?View.VISIBLE:View.GONE);
		mMainView.findViewById(R.id.profileinfo_cancel).setVisibility(on?View.VISIBLE:View.GONE);
		mState = on;
	}

	@Override
	public void onItemClick(AdapterView<?> ad, View v, int pos, long id) {
		// TODO Auto-generated method stub
		
	}

}
