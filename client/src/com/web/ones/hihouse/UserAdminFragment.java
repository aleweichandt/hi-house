package com.web.ones.hihouse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.TextView;
import android.widget.Toast;

public class UserAdminFragment extends ListFragment implements 
	OnItemClickListener{
	
	private UserInfoFragment mUserFragment = null;
	private View mRootView;
	private ListView mList = null;
	private UserListAdapter mAdapter;
	private HiHouse hiHouseAct;
	private ArrayList<User> users;
	private User user;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hiHouseAct = (HiHouse)getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_users, container, false);
		
		mList = (ListView) mRootView.findViewById(android.R.id.list);
		
		//Cargamos lista vacia mientras vuelve el request
		users = new ArrayList<User>();
		mAdapter = new UserListAdapter(getActivity());
		mList.setAdapter(mAdapter);
		
		user = ((HiHouse)getActivity()).getUser(); 
		Command command = new Command(Request.GET_LIST_USERS, true, "users/all?token="+user.getToken(), "");
		hiHouseAct.mHiHouseService.sendCommand(command);
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
		
		return mRootView;
	}
	
	public void loadUsersList(String str) {		
		JSONArray devArray;
		JSONObject userInfo;
    	try{
    		devArray = new JSONArray(str);
    		for(int i=0; i<devArray.length(); i++){
    			userInfo = devArray.getJSONObject(i);
    			User d = new User(userInfo.getInt("id"), userInfo.getString("name"));
    			users.add(d);
    		}
    	}
    	catch(JSONException e){e.printStackTrace();}
    	
    	mAdapter.notifyDataSetChanged();
    	hiHouseAct.setLoadingBarVisibility(View.GONE);
	}
	
	public void refreshDevices() {
		users = new ArrayList<User>();
		Command command = new Command(Request.GET_LIST_USERS, true, "users/all?token="+user.getToken(), "");
		hiHouseAct.mHiHouseService.sendCommand(command);
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
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
		User u = users.get(pos);
		mUserFragment = new UserInfoFragment();
		Bundle args = new Bundle();
		args.putBoolean(UserInfoFragment.ARG_IS_ADD, false);
		args.putString(UserInfoFragment.ARG_USER_NAME, u.getUser());
		args.putInt(UserInfoFragment.ARG_USER_ID, u.getId());
		mUserFragment.setArguments(args);
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		ft.add(R.id.userinfo_container, mUserFragment, UserInfoFragment.class.getName());
		ft.addToBackStack(UserInfoFragment.class.toString());
		ft.commit();
	}
	
	public void mostrarDatos(String str){
		Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
	}
	
	//adapter for list
	private class UserListAdapter extends ArrayAdapter<User> {
		private Context mContext;
		
		public UserListAdapter(Context context) {
			super(context, R.layout.simple_row, users);
			mContext = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.simple_row, parent, false);
			TextView tv = (TextView)rowView.findViewById(R.id.row_name);
			tv.setText(users.get(position).getUser());
			return rowView;
		}
		
		@Override
		public int getCount(){
			return users.size();
		}
	}
}
