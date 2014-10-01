package com.web.ones.hihouse;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class HiHouseService extends Service{
	
	// Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
    	HiHouseService getService() {
            // Return this instance of HiHouseService so clients can call public methods
            return HiHouseService.this;
        }
    }


	@Override
	public void onCreate() {
		//Toast.makeText(this, "The new Service was Created", Toast.LENGTH_SHORT).show();
	}
	
	 @Override
	   public int onStartCommand(Intent intent, int flags, int startId) {
		 //Toast.makeText(this, "Service Start Command", Toast.LENGTH_SHORT).show();
		 return START_STICKY;
	 }
	 
	 @Override
	   public void onDestroy() {
	      super.onDestroy();
	      //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
	 }
	   
	 @Override
	 public IBinder onBind(Intent intent) {
		 //Toast.makeText(this, "Service Bounded", Toast.LENGTH_SHORT).show();
		 return mBinder;
	 }
	 //private boolean a = true;
	public void testMethod(){
		SocketOperator so = new SocketOperator(this);
		//String params = "" + URLEncoder.encode("username","UTF-8")+"="+ URLEncoder.encode("Charly","UTF-8");
		//so.sendRequest(false, "http://192.168.1.110/", "username=Charly");
		//so.sendRequest(false, "http://192.168.1.110:8080/HiHouse/devices/test/state?enabled=false&token=0", "");
		//so.sendRequest(true, "http://192.168.1.110:8080/HiHouse/users/login?id=admin&pwd=1234", " ");
		//http://localhost:8080/HiHouse/users/login?id=admin&pwd=1234
		//http://localhost:8080/HiHouse/devices/test/state?enabled=true&token=0
		//so.sendRequest(false, "http://192.168.1.110/AppServer/", "x=asd");
		so.sendRequest(Request.GET_USER_DEVICES, true, "http://192.168.1.110/AppServer", "");
		//so.sendRequest(Request.SET_DEVICE_STATE, false, "http://192.168.1.110:8080/HiHouse/devices/test/state?enabled="+a+"&token=0", "");
		//a= !a;
		//so.sendRequest(Request.GET_USER_DEVICES, true, "http://192.168.1.110:8080/HiHouse/users/admin/devices?token=1&add_voice_id=true&add_state=true", "");
	}
	public void sendCommand(Command c){
		SocketOperator so = new SocketOperator(this);
		so.sendRequest(c.getType(), c.getMethod(), c.getRequestURL(), c.getParams());
	}
	 
	public class HiHouseTask {
		public static final String NEW_RESPONSE = "hihouse_task.new_response";
		public static final String UPDATE_EXP_LIST = "hihouse_task.update_exp_list";
	}
}