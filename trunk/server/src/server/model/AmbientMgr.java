package server.model;

import java.util.Iterator;
import java.util.List;

import server.model.devices.Device;
import server.model.devices.TermalActuator;
import server.model.devices.TermalSensor;

public class AmbientMgr {
	// Singleton begin
	private static AmbientMgr sInstance = null;
	
	public static AmbientMgr getInstance() {
		if(sInstance == null) {
			sInstance = new AmbientMgr();
		}
		return sInstance;
	}
	// Singleton end
	
	private int mTime;
	private float mRealTemp;
	private float mDesiredTemp;
	private boolean mActuatorsEnabled;
	public AmbientMgr() {
		mTime = 0;
		mRealTemp = -1;
		mDesiredTemp = -1;
	}
	
	public void update(int dt) {
		mTime += dt;
		if(mTime > C.Config.AMBIENT_UPDATE_TIME) {
			mTime -= C.Config.AMBIENT_UPDATE_TIME;
			getRealTemperature();
			if(shouldWork()) {
				updateActuators();
			}
		}
	}
	
	private boolean shouldWork() {
		return mDesiredTemp >= 0  && mRealTemp >=0;
	}
	
	private void getRealTemperature() {
		int count=0;
		float sum=0;
		DBRequestHandler request = new DBRequestHandler();
		List<Object> ids = request.listAllDevicesOfType(Device.DEVICE_TYPE_SN_TERMAL);
		if(!ids.isEmpty()) {
			for(Iterator<Object> it = ids.iterator(); it.hasNext();) {
				String deviceid = (String)it.next();
				TermalSensor sensor = (TermalSensor) Device.getFromDB(deviceid);
				if(sensor.getState()) {
					sum += sensor.getValue();
					count++;
				}
			}
			if(count>0) {
				mRealTemp = sum/(float)count;
			}
		}
	}
	
	private void updateActuators() {
		float diff = mDesiredTemp - mRealTemp;
		boolean shouldEnable = (Math.abs(diff) > C.Config.AMBIENT_MAX_DIFF_DEGREES);
		if(shouldEnable != mActuatorsEnabled) {
			DBRequestHandler request = new DBRequestHandler();
			List<Object> ids = request.listAllDevicesOfType(Device.DEVICE_TYPE_AC_TERMAL);
			if(!ids.isEmpty()) {
				for(Iterator<Object> it = ids.iterator(); it.hasNext();) {
					String deviceid = (String)it.next();
					TermalActuator act = (TermalActuator) Device.getFromDB(deviceid);
					if(act.getState() && shouldEnable) {
						if(diff>0 && act.canHeat()){
							act.heat();
						}else if (diff<0 && act.canCool()){
							act.cool();
						} else {
							act.none();
						}
						act.setState(true);
					} else {
						act.setState(false);
					}
				}
			}
			mActuatorsEnabled = shouldEnable;
		}
	}
	
	public void setTemperature(float desired) {
		mDesiredTemp = desired;
	}
	
	public float getTemperature() {
		getRealTemperature();
		return mRealTemp;
	}
}
