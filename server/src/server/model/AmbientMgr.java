package server.model;

import java.util.Iterator;
import java.util.List;

import server.model.devices.Device;
import server.model.devices.TermalActuator;
import server.model.devices.TermalSensor;

public class AmbientMgr {
	// Singleton begin
	private static AmbientMgr sInstance = null;
	private static int[] sSensorTypes = {Device.DEVICE_TYPE_SN_TERMAL};
	private static int[] sActuatorTypes = {Device.DEVICE_TYPE_AC_TERMAL};
	
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
	private boolean mUpdate;
	public AmbientMgr() {
		mTime = 0;
		mRealTemp = -1;
		mDesiredTemp = -1;
		mUpdate = false;
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
		if(mUpdate) {mUpdate=false;return true;}
		return mDesiredTemp >= 0  && mRealTemp >=0;
	}
	
	private void getRealTemperature() {
		int count=0;
		float sum=0;
		DBRequestHandler request = new DBRequestHandler();
		List<Object> ids = request.listAllDevicesOfType(sSensorTypes);
		if(!ids.isEmpty()) {
			for(Iterator<Object> it = ids.iterator(); it.hasNext();) {
				String deviceid = it.next().toString();
				TermalSensor sensor = (TermalSensor) Device.getFromDB(deviceid);
				if(sensor.getState()) {
					sum += sensor.getValue();
					count++;
				}
			}
			if(count>0) {
				mRealTemp = sum/(float)count;
			}
			else if(mRealTemp!=-1){
				//si no hay sensores activos, actualizamos para que no caliente ni enfrie. 
				mRealTemp = -1;
				mUpdate = true;
			}
		}
	}
	
	private void updateActuators() {
		float diff = mDesiredTemp - mRealTemp;
		DBRequestHandler request = new DBRequestHandler();
		List<Object> ids = request.listAllDevicesOfType(sActuatorTypes);
		if(!ids.isEmpty()) {
			for(Iterator<Object> it = ids.iterator(); it.hasNext();) {
				String deviceid = it.next().toString();
				TermalActuator act = (TermalActuator) Device.getFromDB(deviceid);
				if(act.getState()) {
					if(mDesiredTemp==-1 || mRealTemp==-1) act.none();
					else if(diff > C.Config.AMBIENT_MAX_DIFF_DEGREES && act.canHeat()){
						act.heat();
					}else if (diff < (-C.Config.AMBIENT_MAX_DIFF_DEGREES) && act.canCool()){
						act.cool();
					} else {
						act.none();
					}
					act.setState(true);
				}
			}
		}
	}
	
	public void setTemperature(float desired) {
		mDesiredTemp = desired;
		if(desired==-1) mUpdate = true; //let update when we set desired temp "off"
		mTime = C.Config.AMBIENT_UPDATE_TIME; //force update
	}
	
	public float getTemperature() {
		return mDesiredTemp;
	}
}
