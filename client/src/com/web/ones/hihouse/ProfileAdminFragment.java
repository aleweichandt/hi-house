package com.web.ones.hihouse;

import java.util.ArrayList;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ProfileAdminFragment extends ListFragment implements 
	OnItemClickListener{
	
	private ProfileInfoFragment mProfileFragment = null;
	private View mRootView;
	private ListView mList = null;
	private ArrayAdapter<String> mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_profiles, container, false);
		loadProfilesList();
		
		return mRootView;
	}
	
	private void loadProfilesList() {
		//TODO load real users
		String[] values = new String[] { "Cocina", "Baño", "Cochera",
									"Habitacion 1", "Living" };

	    final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < values.length; ++i) {
	      list.add(values[i]);
	    }
	    mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_row, list);
	    
	    mList = (ListView) mRootView.findViewById(android.R.id.list);
	    mList.setAdapter(mAdapter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mList.setOnItemClickListener(null);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
		String name = mAdapter.getItem(pos);
		/*mProfileFragment = new ProfileInfoFragment(name, false);
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		ft.add(R.id.userinfo_container, mProfileFragment);
		ft.addToBackStack(ProfileInfoFragment.class.toString());
		ft.commit();*/
	}
}
