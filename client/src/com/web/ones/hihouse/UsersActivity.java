package com.web.ones.hihouse;

import java.util.ArrayList;

import com.web.ones.hihouse.UserInfoFragment.OnUserInfoListener;
import com.web.ones.hihouse.VoiceInputButton.OnVoiceCommand;

import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UsersActivity extends ListActivity implements 
	OnItemClickListener,
	OnUserInfoListener,
	OnVoiceCommand{
	
	private UserInfoFragment mUserFragment = null;
	private ListView mList = null;
	private ArrayAdapter<String> mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users);
		loadUsers();
	}
	
	private void loadUsers() {
		//TODO load real users
		String[] values = new String[] { "Jose", "Ines", "Juancito",
		        "Pedro", "Betina" };

	    final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < values.length; ++i) {
	      list.add(values[i]);
	    }
	    mAdapter = new ArrayAdapter<String>(this, R.layout.user_row, list);
	    
	    mList = (ListView) this.findViewById(android.R.id.list);
	    mList.setAdapter(mAdapter);
	    mList.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.users, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
	
	@Override
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
	public void onVoiceInputInteraction() {
		// TODO Auto-generated method stub
		
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
