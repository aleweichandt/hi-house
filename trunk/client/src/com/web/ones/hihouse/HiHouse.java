package com.web.ones.hihouse;


import com.web.ones.hihouse.HiHouseService.HiHouseTask;
import com.web.ones.hihouse.HiHouseService.LocalBinder;
import com.web.ones.hihouse.VoiceTranslation.OnVoiceCommand;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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

    //para manejar el HiHouseService
    HiHouseService mHiHouseService;
    boolean mBound = false;
    
    //DB
    DBHelper mydb;
    
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to HiHouseService, cast the IBinder and get HiHouseService instance
            LocalBinder binder = (LocalBinder) service;
            mHiHouseService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

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
        
        Fragment fragment = new VoiceTranslation();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.voiceButton_frame, fragment).commit();
        
        //arrancamos el HiHouseService explicitamente para que sea un Started Service
        startService(new Intent(this, HiHouseService.class));
        // Bind to HiHouseService
        Intent intent = new Intent(this, HiHouseService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        
        // Message handling - from service:
        final IntentFilter myFilter = new IntentFilter(HiHouseTask.NEW_RESPONSE);
        registerReceiver(mReceiver, myFilter);
        
        // DB operations
        mydb = new DBHelper(this);
        
        //insertamos alguns devices de prueba
        mydb.insertDevice(1, "luz cocina");
        mydb.insertDevice(2, "luz living");
        mydb.insertDevice(3, "puerta principal");
        mydb.insertDevice(4, "alarma central");
	}
	
	@Override
    protected void onStart() {
        super.onStart();
    }

	@Override
    protected void onStop() {
        super.onStop();
    }
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
            //Toast.makeText(this, "Service Unbound", Toast.LENGTH_SHORT).show();
        }
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
		//TODO Callback de VoiceTranslation fragment
		//Toast.makeText(this, "asd!", Toast.LENGTH_SHORT).show();
		mHiHouseService.testMethod();
	}
	
	public void stopService(View v){
		stopService(new Intent(this, HiHouseService.class));
	}
	public void callServiceMethod(View v){
		if (mBound) {
            // Call a method from the HiHouseService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            mHiHouseService.testMethod();
        }

	}
	
	private class DataUpdateReceiver extends BroadcastReceiver {
 
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(HiHouseTask.NEW_RESPONSE)) {
                // do something with the tweet
            	Toast.makeText(context, "Broadcast!", Toast.LENGTH_SHORT).show();
            }
 
        }
    }
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final TextView responseFromService = (TextView) findViewById(R.id.broadcast);
            responseFromService.setText(intent.getCharSequenceExtra("data"));
        }
	};

}
