package com.web.ones.hihouse;

import java.util.ArrayList;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class UserAdminFragment extends ListFragment implements 
	OnItemClickListener{
	
	private UserInfoFragment mUserFragment = null;
	private View mRootView;
	private ListView mList = null;
	private ArrayAdapter<String> mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_users, container, false);
		loadUsersList();
		
		return mRootView;
	}
	
	private void loadUsersList() {
		//TODO load real users
		String[] values = new String[] { "Jose", "Ines", "Juancito",
		        "Pedro", "Betina" };
		
		User user = ((HiHouse)getActivity()).getUser(); 
		Command command = new Command(Request.GET_LIST_USERS, true, "users/all?token="+user.getToken(), "");
		((HiHouse)getActivity()).mHiHouseService.sendCommand(command);

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
		mList.setOnItemClickListener(null);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		mList.setOnItemClickListener(this);
		super.onResume();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
		String name = mAdapter.getItem(pos);
		mUserFragment = new UserInfoFragment(name, false);
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		ft.add(R.id.userinfo_container, mUserFragment);
		ft.addToBackStack(UserInfoFragment.class.toString());
		ft.commit();
	}
	
	public void mostrarDatos(String str){
		Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
	}
}
