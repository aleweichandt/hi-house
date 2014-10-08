package com.web.ones.hihouse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class ProfileAdminFragment extends ListFragment implements OnItemClickListener{
	
	private ProfileInfoFragment mProfileFragment = null;
	private View mRootView;
	private ListView mList = null;
	private ProfileListAdapter mAdapter;
	private HiHouse hiHouseAct;
	private ArrayList<Profile> profiles;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hiHouseAct = (HiHouse)getActivity();
		profiles = new ArrayList<Profile>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_profiles, container, false);

		mList = (ListView) mRootView.findViewById(android.R.id.list);
		
		//Cargamos lista vacia mientras vuelve el request
		mAdapter = new ProfileListAdapter(getActivity());
		mList.setAdapter(mAdapter);
		
		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.GET_ALL_PROFILES, true, "profiles/all", "token="+hiHouseAct.getUser().getToken()));
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
		
		return mRootView;
	}

	public void refreshProfiles() {
		profiles = new ArrayList<Profile>();
		hiHouseAct.mHiHouseService.sendCommand(new Command(Request.GET_ALL_PROFILES, true, "profiles/all", "token="+hiHouseAct.getUser().getToken()));
		hiHouseAct.setLoadingBarVisibility(View.VISIBLE);
	}
	
	public void loadProfilesList(String str) {
		JSONArray profArray;
		JSONObject profInfo;
		try{
			profArray = new JSONArray(str);
			for(int i=0; i<profArray.length(); i++){
    			profInfo = profArray.getJSONObject(i);
    			Profile p = new Profile(profInfo.getInt("id"), profInfo.getString("name"));
    			profiles.add(p);
    		}
		}
		catch(JSONException e){e.printStackTrace();}

		mAdapter.notifyDataSetChanged();
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
		mProfileFragment = new ProfileInfoFragment();
		Bundle args = new Bundle();
		args.putBoolean(ProfileInfoFragment.ARG_IS_ADD, false);
		args.putString(ProfileInfoFragment.ARG_PROFILE_NAME, profiles.get(pos).getName());
		args.putInt(ProfileInfoFragment.ARG_PROFILE_ID, profiles.get(pos).getId());
		mProfileFragment.setArguments(args);
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		ft.add(R.id.userinfo_container, mProfileFragment, ProfileInfoFragment.class.getName());
		ft.addToBackStack(ProfileInfoFragment.class.toString());
		ft.commit();
	}
	
	//adapter for list
	private class ProfileListAdapter extends ArrayAdapter<Profile> {
		private Context mContext;
		
		public ProfileListAdapter(Context context) {
			super(context, R.layout.simple_row, profiles);
			mContext = context;
		}
			
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.simple_row, parent, false);
			TextView tv = (TextView)rowView.findViewById(R.id.row_name);
			tv.setText(profiles.get(position).getName());
			return rowView;
		}
		
		@Override
		public int getCount(){
			return profiles.size();
		}
	}
}
