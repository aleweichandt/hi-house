package server.model.devices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import server.model.DBRequestHandler;

public abstract class Device {

	static final int DEVICE_TYPE_SN_TERMAL = 0;
	static final int DEVICE_TYPE_SN_LIGHT = 1;
	static final int DEVICE_TYPE_AC_LIGHT = 2;
	static final int DEVICE_TYPE_AC_TERMAL = 3;
	static final int DEVICE_TYPE_AC_DOOR = 4;
	
	
	public static Device getFromDB(String deviceid) {
		DBRequestHandler request = new DBRequestHandler();
		Map<String, Object> values = request.getDevice(deviceid);
		if(!values.isEmpty()){
			String id = (String)values.get("ID_Dispositivo");
			String ambient = (String)values.get("Ambiente");
			int type = Integer.parseInt((String)values.get("Tipo"));
			String voiceid = (String)values.get("Descripcion_Ejec_Voz");
			boolean state = (Boolean)values.get("Estado");
			int pin1 = Integer.parseInt((String)values.get("Pin1"));
			int pin2 = Integer.parseInt((String)values.get("Pin2"));
			int pin3 = Integer.parseInt((String)values.get("Pin3"));
			
			switch(type) {
			case DEVICE_TYPE_AC_LIGHT:
				return new LightActuator(id, ambient, voiceid, state, pin1, pin2, pin3);
			case DEVICE_TYPE_AC_DOOR:
				return new DoorActuator(id, ambient, voiceid, state, pin1, pin2, pin3);
			case DEVICE_TYPE_AC_TERMAL:
				return new TermalActuator(id, ambient, voiceid, state, pin1, pin2, pin3);
			case DEVICE_TYPE_SN_TERMAL:
				return new TermalSensor(id, ambient, voiceid, state, pin1, pin2, pin3);
			case DEVICE_TYPE_SN_LIGHT:
				return new LightSensor(id, ambient, voiceid, state, pin1, pin2, pin3);
			default:
				return null;
			}	
		}
		return null;
	}
	
	protected String mId = null;
	protected String mName = null;
	protected String mVoiceId = null;
	protected boolean mState = false;
	protected List<Integer> mPins = null;
	protected List<Integer> mPinValues = null;
	
	public Device(String id, String name, String voiceid,
				  boolean state, int pin1, int pin2, int pin3) {
		mId = id;
		mName = name;
		mVoiceId = voiceid;
		mState = state;
		mPins = new ArrayList<Integer>();
		mPinValues = new ArrayList<Integer>();
		if(pin1>=0) {
			mPins.add(pin1);
			mPinValues.add(0);
		}
		if(pin2>=0) {
			mPins.add(pin2);
			mPinValues.add(0);
		}
		if(pin3>=0) {
			mPins.add(pin3);
			mPinValues.add(0);
		}
	}
	
	public int getPin(int id) {
		if(mPins.size() > id && id >= 0) {
			return mPins.get(id);
		}
		return -1;
	}
	
	public int getPinsAmount() {
		return mPins.size();
	}
	
	public abstract boolean setState(boolean state);
	public abstract int getValueType();
}
