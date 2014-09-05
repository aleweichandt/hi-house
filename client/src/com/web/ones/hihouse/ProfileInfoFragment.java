package com.web.ones.hihouse;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

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
		ProfileInfoAdapter adapter = new ProfileInfoAdapter(getActivity(), list);
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
		RadioButton r = (RadioButton)v.findViewById(R.id.dev_row_check);
		r.setChecked(!r.isChecked());
	}
//adapter for list
	private class ProfileInfoAdapter extends ArrayAdapter<String> {
		private ArrayList<String> mData;
		private Context mContext;
		
		public ProfileInfoAdapter(Context context, List<String> objects) {
			super(context, R.layout.profileinfo_device_row, objects);
			mData = (ArrayList<String>) objects;
			mContext = context;
		}
			
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
										.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.profileinfo_device_row, parent, false);
			TextView tv = (TextView)rowView.findViewById(R.id.dev_row_name);
			tv.setText(mData.get(position));
			return rowView;
		}
	}
}
