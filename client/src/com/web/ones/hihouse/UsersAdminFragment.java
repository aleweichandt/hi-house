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

public class UsersAdminFragment extends ListFragment implements 
	OnItemClickListener{
	
	private UserInfoFragment mUserFragment = null;
	private View mRootView;
	private ListView mList = null;
	private ArrayAdapter<String> mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_users, container, false);
        int i = getArguments().getInt(HiHouse.ARG_NUMBER);
        String title = getResources().getStringArray(R.array.nav_drawer_items)[i];

        getActivity().setTitle(title);
		loadUsersList();
		
		return mRootView;
	}
	
	private void loadUsersList() {
		//TODO load real users
		String[] values = new String[] { "Jose", "Ines", "Juancito",
		        "Pedro", "Betina" };

	    final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < values.length; ++i) {
	      list.add(values[i]);
	    }
	    mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.user_row, list);
	    
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
		mUserFragment = UserInfoFragment.newInstance(name);
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		ft.add(R.id.userinfo_container, mUserFragment);
		ft.addToBackStack(UserInfoFragment.class.toString());
		ft.commit();
	}
	
	public void onBackPressed() {
		removeUserView();
	}
	
	private void removeUserView() {
		if(mUserFragment != null) {
	    	FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
	    	ft.remove(mUserFragment);
	    	ft.commit();
	    	mList.setVisibility(View.VISIBLE);
	    }
	}
}
