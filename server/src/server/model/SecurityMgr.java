package server.model;

import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;

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
	private int mTimeToSendAlert;
	public SecurityMgr() {
		mTime = 0;
		mEnabled = false;
		mTimeToSendAlert = 0;
	}
	
	public void update(int dt) {
		mTime += dt;
		if(mTimeToSendAlert > 0) {
			mTimeToSendAlert -= dt;
		}
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
		if(mTimeToSendAlert <= 0) {
			User dest = getAlertDestination();
			if(dest != null) {
				String notificationId = dest.getNotificationID();
				if(notificationId != null && !notificationId.isEmpty()) {
					//send alert to dest
					JsonObject data = Json.createObjectBuilder()
										  .add("message_id", "security_alert")
										  .build();
					AndroidNotification an = new AndroidNotification();
					if(an.send(notificationId, data)){
						mTimeToSendAlert += C.Config.SECURITY_ALERT_TIME_OFFSET;
					}
				}
			}
		}
	}
	
	public void setState(boolean enabled) {
		mEnabled = enabled;
	}
	
	public boolean getState() {
		return mEnabled;
	}
	
	public boolean setAlertDestination(User usr) {
		User dest = getAlertDestination();
		if(dest != null) {
			if(!dest.unsetReceptor())
				return false;
		}
		return usr.setAsReceptor();
	}
	
	public User getAlertDestination() {
		return User.getAlertReceiver();
	}
}
