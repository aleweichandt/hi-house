package com.web.ones.hihouse;


import com.web.ones.hihouse.VoiceInputButton.OnVoiceCommand;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class HiHouse extends Activity implements OnVoiceCommand{
	private static final int DRAWER_MENU_INDEX_LOGIN = 0;
	private static final int DRAWER_MENU_INDEX_MY_DEVICES = 1;
	private static final int DRAWER_MENU_INDEX_ADD_USER = 2;
	private static final int DRAWER_MENU_INDEX_USERS = 3;
	private static final int DRAWER_MENU_INDEX_ADD_PROFILE = 4;
	private static final int DRAWER_MENU_INDEX_PROFILES = 5;
	private static final int DRAWER_MENU_INDEX_ADD_DEVICE = 6;
	private static final int DRAWER_MENU_INDEX_DEVICES = 7;
	private static final int DRAWER_MENU_INDEX_SIMULATOR = 8;
	
	private String[] menuItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle,mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hi_house);

		mTitle = getTitle();
		mDrawerTitle = getString(R.string.drawer_title);
		menuItems = getResources().getStringArray(R.array.nav_drawer_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, menuItems));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(DRAWER_MENU_INDEX_LOGIN);
        }
        
        Fragment fragment = new VoiceInputButton();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.voiceButton_frame, fragment).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hi_house, menu);
		return true;
	}

	/* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
           return true;
		}
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    
    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
    	Fragment fragment = null;
    	Bundle args = new Bundle();
    	boolean addToBackStack = false;
    	String backStackTag = "";
    	switch(position) {
    	case DRAWER_MENU_INDEX_LOGIN:
    		fragment = new LoginFragment();
    		break;
    	case DRAWER_MENU_INDEX_MY_DEVICES:
    		fragment = new MyDevicesFragment();
    		break;
    	case DRAWER_MENU_INDEX_ADD_USER:
    		fragment = new UserInfoFragment("Nuevo", true);
    		addToBackStack = true;
    		backStackTag = UserInfoFragment.class.toString();
    		break;
    	case DRAWER_MENU_INDEX_USERS:
    		fragment = new UserAdminFragment();
    		break;
    	case DRAWER_MENU_INDEX_ADD_PROFILE: 
    		fragment = new ProfileInfoFragment("Nuevo", true);
    		addToBackStack = true;
    		backStackTag = ProfileInfoFragment.class.toString();
    		break;
    	case DRAWER_MENU_INDEX_PROFILES: 
    		fragment = new ProfileAdminFragment();
    		break;
    	case DRAWER_MENU_INDEX_ADD_DEVICE:
    		fragment = new DeviceInfoFragment("Nuevo", true);
    		addToBackStack = true;
    		backStackTag = DeviceInfoFragment.class.toString();
    		break;
    	case DRAWER_MENU_INDEX_DEVICES:
    		fragment = new DeviceAdminFragment();
    		break;
    	case DRAWER_MENU_INDEX_SIMULATOR:
    		//TODO add fragment
    		fragment = new SimulatorFragment();
    		break;
    	default:
    		Toast.makeText(this, "Error de indice", Toast.LENGTH_SHORT).show();
    		break;
    	}
    	if(fragment != null) {
	    	fragment.setArguments(args);
	        FragmentManager fragmentManager = getFragmentManager();
	        if(addToBackStack) {
		        fragmentManager.beginTransaction()
		        			   .add(R.id.content_frame, fragment)
		        			   .addToBackStack(backStackTag)
		                       .commit();
	        } else {
	        	fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
	        }
	        mDrawerList.setItemChecked(position, true);
	        setTitle(menuItems[position]);
	        mDrawerLayout.closeDrawer(mDrawerList);
    	}
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            if ( this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
                this.mDrawerLayout.closeDrawer(this.mDrawerList);
            }
            else {
                this.mDrawerLayout.openDrawer(this.mDrawerList);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

	@Override
	public void onVoiceInputInteraction() {
		//TODO remove this (testing now)
		//voiceTranslation.speak();
		//speak();
	}
}
