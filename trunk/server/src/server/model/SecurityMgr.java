package server.model;

import java.util.Iterator;
import java.util.List;

import server.model.devices.Device;
import server.model.devices.Sensor;

public class SecurityMgr {
	// Singleton begin
	private static SecurityMgr sInstance = null;
	private static int[] sSensorTypes = {Device.DEVICE_TYPE_SN_TERMAL, Device.DEVICE_TYPE_SN_LIGHT};
	
	public static SecurityMgr getInstance() {
		if(sInstance == null) {
			sInstance = new SecurityMgr();
		}
		return sInstance;
	}
	// Singleton end
	
	private int mTime;
	private boolean mEnabled;
	public SecurityMgr() {
		mTime = 0;
		mEnabled = false;
	}
	
	public void update(int dt) {
		mTime += dt;
		if(mTime > C.Config.SECURITY_UPDATE_TIME) {
			mTime -= C.Config.SECURITY_UPDATE_TIME;
			if(mEnabled) {
				checkState();
			}
		}
	}
	
	private void checkState() {
		DBRequestHandler request = new DBRequestHandler();
		List<Object> ids = request.listAllDevicesOfType(sSensorTypes);
		if(!ids.isEmpty()) {
			for(Iterator<Object> it = ids.iterator(); it.hasNext();) {
				String deviceid = (String)it.next();
				Sensor sensor = (Sensor) Device.getFromDB(deviceid);
				if(sensor.getState()) {
					float value = sensor.getValue();
					if(sensor.isWarnValue(value)) {
						sendAlert();
						break;
					}
				}
			}
		}
	}
	
	private void sendAlert() {
		//TODO send alert to dest user
	}
	
	public void setState(boolean enabled) {
		mEnabled = enabled;
	}
	
	public boolean getState() {
		return mEnabled;
	}
	
	public void setAlertDestination(User usr) {
		//TODO update users alert destination to true, disable others
	}
	
	public User getAlertDestination() {
		//TODO return alert destination user if someone
		return null;
	}
}
