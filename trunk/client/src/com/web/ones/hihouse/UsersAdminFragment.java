package com.web.ones.hihouse;

import java.util.ArrayList;

import com.web.ones.hihouse.UserInfoFragment.OnUserInfoListener;

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
	OnItemClickListener,
	OnUserInfoListener{
	
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
		loadUsers();
		
		return mRootView;
	}
	
	private void loadUsers() {
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
	    mList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
		String name = mAdapter.getItem(pos);
		mUserFragment = UserInfoFragment.newInstance(name);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.userinfo_container, mUserFragment);
		ft.commit();
		mList.setVisibility(View.GONE);
	}
	
	public void onBackPressed() {
		removeUserView();
	}
	
	private void removeUserView() {
		if(mUserFragment != null) {
	    	FragmentTransaction ft = getFragmentManager().beginTransaction();
	    	ft.remove(mUserFragment);
	    	ft.commit();
	    	mList.setVisibility(View.VISIBLE);
	    }
	}

	@Override
	public void onEndEdition() {
		removeUserView();
	}
	
	public UserInfoFragment getUserInfoFragment() { return mUserFragment;}
	
//handled by fragment
	public void onEditPressed(View v) {mUserFragment.onEditPressed(v);}
	public void onConfirmEdition(View v) {mUserFragment.onConfirmEdition(v);}
	public void onCancelEdition(View v) {mUserFragment.onCancelEdition(v);}
	public void onDeletePressed(View v) {mUserFragment.onDeletePressed(v);}

	public void onProfilesPressed(View v) {mUserFragment.onProfilesPressed(v);}
}
