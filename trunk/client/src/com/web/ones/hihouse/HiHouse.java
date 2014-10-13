package com.web.ones.hihouse;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
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
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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
	
//GCM Notification
	public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String SENDER_ID = "874005567993";
    String mGCMRegistrationId;
	
	private String[] menuItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle,mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private ProgressBar mainLoadingBar;
    private int menuLastPosition = -1;
    
    public void setLoadingBarVisibility(int v){
    	mainLoadingBar.setVisibility(v);
    }
    
    private User user; 
    public User getUser(){
    	return user;
    }

    //para manejar el HiHouseService
    HiHouseService mHiHouseService;
    boolean mBound = false;
    
    
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to HiHouseService, cast the IBinder and get HiHouseService instance
            LocalBinder binder = (LocalBinder) service;
            mHiHouseService = binder.getService();
            mBound = true;
            
            //una vez que el se hace bind del servicio cargo mis dispositivos
            selectItem(DRAWER_MENU_INDEX_MY_DEVICES);
           //GCM Notifications
            if(!getUser().canReceiveNotifications() && mGCMRegistrationId != null) {
            	SendRegistrationId(mGCMRegistrationId);
            }
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

		//Recibimos los parametros del Login
		Intent intentLogin = getIntent();
		String user = intentLogin.getStringExtra(LoginActivity.EXTRA_USER);
		String pass = intentLogin.getStringExtra(LoginActivity.EXTRA_PASS);
		String token = intentLogin.getStringExtra(LoginActivity.EXTRA_TOKEN);
		boolean admin = intentLogin.getBooleanExtra(LoginActivity.EXTRA_ADMIN, false);
		boolean save = intentLogin.getBooleanExtra(LoginActivity.EXTRA_SAVE, false);
		
		this.user = new User(user, pass, token, admin);
		
		mTitle = getTitle();
		mDrawerTitle = getString(R.string.drawer_title);
		menuItems = getResources().getStringArray(this.user.isAdmin()?R.array.nav_drawer_items_admin:R.array.nav_drawer_items_default);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mainLoadingBar = (ProgressBar) findViewById(R.id.main_loading_bar);

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
            //selectItem(DRAWER_MENU_INDEX_LOGIN);
        }
        
        Fragment fragment = new VoiceTranslation();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.voiceButton_frame, fragment).commit();
        
        //arrancamos el HiHouseService explicitamente para que sea un Started Service
        startService(new Intent(this, HiHouseService.class));
        // Bind to HiHouseService
        Intent intent = new Intent(this, HiHouseService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        
    //GCM Notification
        if (checkPlayServices()) {
        	mGCMRegistrationId = getRegistrationId(getApplicationContext());
            if (mGCMRegistrationId.isEmpty()) {
                registerInBackground();
            } else {
            	SendRegistrationId(mGCMRegistrationId);
            }
        }
	}
	
	@Override
	protected void onResume(){
		// Message handling - from service:
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(HiHouseTask.NEW_RESPONSE));
		super.onResume();
	}
	
	@Override
	protected void onPause(){
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
		super.onPause();
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
    	String fragmentTag = null;//para hacer FragmentManager.findFragmentByTag(String)
    	if(position==menuLastPosition) return;
    	menuLastPosition = position;
    	Bundle args = new Bundle();
    	boolean addToBackStack = false;
    	String backStackTag = "";
    	switch(position) {
    	case DRAWER_MENU_INDEX_LOGIN:
    		fragment = new LoginFragment();
    		break;
    	case DRAWER_MENU_INDEX_MY_DEVICES:
    		fragment = new MyDevicesFragment();
    		fragmentTag = MyDevicesFragment.class.getName();
    		break;
    	case DRAWER_MENU_INDEX_ADD_USER:
    		fragment = new UserInfoFragment("Nuevo", true);
    		fragmentTag = UserInfoFragment.class.getName();
    		addToBackStack = true;
    		backStackTag = UserInfoFragment.class.toString();
    		break;
    	case DRAWER_MENU_INDEX_USERS:
    		if(this.user.isAdmin()) {
    			fragment = new UserAdminFragment();
    			fragmentTag = UserAdminFragment.class.getName();
    		} else {
    			fragment = new UserInfoFragment("yo", true);
    			fragmentTag = UserInfoFragment.class.getName();
    		}
    		break;
    	case DRAWER_MENU_INDEX_ADD_PROFILE: 
    		fragment = new ProfileInfoFragment();
    		args.putBoolean(ProfileInfoFragment.ARG_IS_ADD, true);
    		fragmentTag = ProfileInfoFragment.class.getName();
    		addToBackStack = true;
    		backStackTag = ProfileInfoFragment.class.toString();
    		break;
    	case DRAWER_MENU_INDEX_PROFILES: 
    		fragment = new ProfileAdminFragment();
    		fragmentTag = ProfileAdminFragment.class.getName();
    		addToBackStack = true;
    		backStackTag = ProfileAdminFragment.class.toString();
    		break;
    	case DRAWER_MENU_INDEX_ADD_DEVICE:
    		fragment = new DeviceInfoFragment();
    		args.putBoolean(DeviceInfoFragment.ARG_IS_ADD, true);
    		fragmentTag = DeviceInfoFragment.class.getName();
    		addToBackStack = true;
    		backStackTag = DeviceInfoFragment.class.toString();
    		break;
    	case DRAWER_MENU_INDEX_DEVICES:
    		fragment = new DeviceAdminFragment();
    		fragmentTag = DeviceAdminFragment.class.getName();
    		addToBackStack = true;
    		backStackTag = DeviceAdminFragment.class.toString();
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
	        	fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		        fragmentManager.beginTransaction()
		        			   .replace(R.id.content_frame, fragment, fragmentTag)
		        			   .addToBackStack(backStackTag)
		                       .commit();
	        } else {
	        	fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, fragmentTag)
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
		//mHiHouseService.testMethod();
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
	
//GCM Notification
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	       return false;
	    }
	    return true;
	}
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        return "";
	    }
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        return "";
	    }
	    return registrationId;
	}
	
	private void registerInBackground() {
	    new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
	            	GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
	            	mGCMRegistrationId = gcm.register(SENDER_ID);
	            	storeRegistrationId(getApplicationContext(), mGCMRegistrationId);
	            	SendRegistrationId(mGCMRegistrationId);
	            } catch (Exception ex) {
	            	ex.printStackTrace();
	            }
				return null;
			}
	    }.execute(null, null, null);
	}
	
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	
	private SharedPreferences getGCMPreferences(Context context) {
	    return getSharedPreferences(HiHouse.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
	
	private void SendRegistrationId(String id) {
		if(mHiHouseService == null) {
			return;
		}
		mHiHouseService.sendCommand(new Command(Request.SET_NOTIFICATION_ID,
												false,
												"users/me/notification?token="+getUser().getToken(),
												id));
	}
	
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	int rc = intent.getIntExtra("responseCode", 0);
        	Fragment frag = null;
        	if(rc==401){
    			//se vencio el token. Cerramos actividad y mandamos al login.
    			Toast.makeText(context, "Sesión expirada. Vuelva a ingresar.", Toast.LENGTH_SHORT).show();
    			Intent activityIntent = new Intent(context, LoginActivity.class);
    			startActivity(activityIntent);
    	    	finish();
    		} else
        	switch(intent.getIntExtra("type",-1)){
        	case Request.SET_DEVICE_STATE:
        		if(rc==200){
        			if(user.updateDevice(intent.getCharSequenceExtra("data").toString())){
        				frag = getFragmentManager().findFragmentByTag(MyDevicesFragment.class.getName());
        				try{((MyDevicesFragment)frag).updateExpList();}catch(ClassCastException e){}
        			}
        		}
        		else{
        			Toast.makeText(context, "Error al actualizar estado", Toast.LENGTH_SHORT).show();
        		}
        		mainLoadingBar.setVisibility(View.GONE);
        		break;
        	case Request.GET_USER_DEVICES:
        		if(rc==500){
        			Toast.makeText(context, "Error al recuperar los dispositivos", Toast.LENGTH_SHORT).show();
        			//TODO manejar el error, permitir traer nuevamente (ej refresh btn)
        		}
        		else if(rc==200){
        			if(user.setProfilesAndDevices(intent.getCharSequenceExtra("data").toString())){
        				frag = getFragmentManager().findFragmentByTag(MyDevicesFragment.class.getName());
                		if(frag!=null) try{((MyDevicesFragment)frag).updateExpList();}catch(ClassCastException e){}
		        		mainLoadingBar.setVisibility(View.GONE);
        			}
        			else Toast.makeText(context, "Error al procesar los dispositivos", Toast.LENGTH_SHORT).show();
        		}
        		break;
        	case Request.ERROR:
        		Toast.makeText(context, "Hubo un error en el request", Toast.LENGTH_SHORT).show();
        		mainLoadingBar.setVisibility(View.GONE);
        		break;
        	case Request.GET_LIST_USERS:
        		frag = getFragmentManager().findFragmentByTag(UserAdminFragment.class.getName());
        		if(frag!=null){
	        		try{
	        			((UserAdminFragment)frag).mostrarDatos(intent.getCharSequenceExtra("data").toString());
	        		}
	        		catch(ClassCastException e){} //esta bien xq el fragmento activo es de otro tipo no y actualizamos nada
        		}
        		//mainLoadingBar.setVisibility(View.GONE);
        		break;
        	case Request.GET_DESIRED_TEMP:
        		frag = getFragmentManager().findFragmentByTag(MyDevicesFragment.class.getName());
        		if(frag!=null){
        			try{((MyDevicesFragment)frag).updateTemp(rc==200?intent.getCharSequenceExtra("data").toString():"");}catch(ClassCastException e){}
        		}
        		break;
        	case Request.SET_DESIRED_TEMP:
        		frag = getFragmentManager().findFragmentByTag(MyDevicesFragment.class.getName());
        		if(frag!=null){
        			try{((MyDevicesFragment)frag).setLastTemp(rc==200?true:false);}catch(ClassCastException e){}
        		}
        		break;
        	case Request.GET_ALL_DEVICES:
        		if(rc==200){
	        		frag = getFragmentManager().findFragmentByTag(ProfileInfoFragment.class.getName());
	        		if(frag!=null){
	        			try{((ProfileInfoFragment)frag).loadDevices(intent.getStringExtra("data"));}catch(ClassCastException e){}
	        		}
	        		frag = getFragmentManager().findFragmentByTag(DeviceAdminFragment.class.getName());
	        		if(frag!=null){
	        			try{((DeviceAdminFragment)frag).loadDevicesList(intent.getStringExtra("data"));}catch(ClassCastException e){}
	        		}
        		}
        		else{
        			Toast.makeText(context, "Error al cargar dispositivos", Toast.LENGTH_SHORT).show();
        			mainLoadingBar.setVisibility(View.GONE);
        		}
        		break;
        	case Request.GET_ALL_PROFILES:
        		if(rc==200){
	        		frag = getFragmentManager().findFragmentByTag(ProfileAdminFragment.class.getName());
	        		if(frag!=null){
	        			try{((ProfileAdminFragment)frag).loadProfilesList(intent.getStringExtra("data"));}catch(ClassCastException e){}
	        		}
        		}
        		else{
        			Toast.makeText(context, "Error al cargar perfiles", Toast.LENGTH_SHORT).show();
        		}
        		mainLoadingBar.setVisibility(View.GONE);
        		break;
        	case Request.GET_PROFILE_DEVICES:
        		if(rc==200){
	        		frag = getFragmentManager().findFragmentByTag(ProfileInfoFragment.class.getName());
	        		if(frag!=null){
	        			try{((ProfileInfoFragment)frag).checkDevices(intent.getStringExtra("data"));}catch(ClassCastException e){}
	        		}
        		}
        		else{
        			Toast.makeText(context, "Error al cargar dispositivos", Toast.LENGTH_SHORT).show();
        		}
        		mainLoadingBar.setVisibility(View.GONE);
        		break;
        	case Request.ADD_PROFILE:
        		frag = getFragmentManager().findFragmentByTag(ProfileInfoFragment.class.getName());
        		if(frag!=null){
        			try{((ProfileInfoFragment)frag).addProfileResult(rc==200?true:false);}catch(ClassCastException e){}
        		}
        		mainLoadingBar.setVisibility(View.GONE);
        		break;
        	case Request.UPDATE_PROFILE:
        		frag = getFragmentManager().findFragmentByTag(ProfileInfoFragment.class.getName());
        		if(frag!=null){
        			try{
        				((ProfileInfoFragment)frag).updateProfileResult(rc==200?true:false);
        				frag = getFragmentManager().findFragmentByTag(ProfileAdminFragment.class.getName());
        				((ProfileAdminFragment)frag).refreshProfiles();
    				}catch(ClassCastException e){}
        		}
        		break;
        	case Request.DELETE_PROFILE:
        		frag = getFragmentManager().findFragmentByTag(ProfileInfoFragment.class.getName());
        		if(frag!=null){
        			try{
        				((ProfileInfoFragment)frag).deleteProfileResult(rc==200?true:false);
        				frag = getFragmentManager().findFragmentByTag(ProfileAdminFragment.class.getName());
        				((ProfileAdminFragment)frag).refreshProfiles();
        			}catch(ClassCastException e){}
        		}
        		break;
        	case Request.ADD_DEVICE:
        		frag = getFragmentManager().findFragmentByTag(DeviceInfoFragment.class.getName());
        		if(frag!=null){
        			try{
        				((DeviceInfoFragment)frag).addDeviceResult(rc==200?true:false);
        				frag = getFragmentManager().findFragmentByTag(DeviceAdminFragment.class.getName());
        				((DeviceAdminFragment)frag).refreshDevices();
        				}catch(ClassCastException e){}
        		}
        		mainLoadingBar.setVisibility(View.GONE);
        		break;
        	case Request.GET_DEVICE:
        		if(rc==200){
	        		frag = getFragmentManager().findFragmentByTag(DeviceInfoFragment.class.getName());
	        		if(frag!=null){
	        			try{((DeviceInfoFragment)frag).updateDeviceInfo(intent.getStringExtra("data"));}catch(ClassCastException e){}
	        		}
        		}
        		else{
        			Toast.makeText(context, "Error al cargar dispositivo", Toast.LENGTH_SHORT).show();
        		}
        		mainLoadingBar.setVisibility(View.GONE);
        		break;
        	case Request.UPDATE_DEVICE:
        		frag = getFragmentManager().findFragmentByTag(DeviceInfoFragment.class.getName());
        		if(frag!=null){
        			try{
        				((DeviceInfoFragment)frag).updateDeviceResult(rc==200?true:false);
        				frag = getFragmentManager().findFragmentByTag(DeviceAdminFragment.class.getName());
        				((DeviceAdminFragment)frag).refreshDevices();
    				}catch(ClassCastException e){}
        		}
        		break;
        	case Request.DELETE_DEVICE:
        		frag = getFragmentManager().findFragmentByTag(DeviceInfoFragment.class.getName());
        		if(frag!=null){
        			try{
        				((DeviceInfoFragment)frag).deleteDeviceResult(rc==200?true:false);
        				frag = getFragmentManager().findFragmentByTag(DeviceAdminFragment.class.getName());
        				((DeviceAdminFragment)frag).refreshDevices();
        			}catch(ClassCastException e){}
        		}
        		break;
        //GCM Notification
        	case Request.SET_NOTIFICATION_ID:
        		if(rc==200) {
        			getUser().setAllowNotifications(true);
        		} else {
        			//TODO controlar el error, quiza reenviar, pero no lo veo necesario
        		}
        		break;
        	}
        }
	};

}
