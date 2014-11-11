package server.model.devices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import server.model.DBRequestHandler;

public abstract class Device {

	public static final int DEVICE_TYPE_SN_TERMAL = 0;
	public static final int DEVICE_TYPE_SN_LIGHT = 1;
	public static final int DEVICE_TYPE_AC_LIGHT = 2;
	public static final int DEVICE_TYPE_AC_TERMAL = 3;
	public static final int DEVICE_TYPE_AC_DOOR = 4;
	public static final int DEVICE_TYPE_SN_DOOR = 5;
	
	public static Device createFromType(String id, String name, int type, String voiceid,
				 						boolean state, int pin1, int pin2, int pin3,
				 						String param1, String param2) {
		switch(type) {
		case DEVICE_TYPE_AC_LIGHT:
			return new LightActuator(id, name, voiceid, state, pin1, pin2, pin3);
		case DEVICE_TYPE_AC_DOOR:
			return new DoorActuator(id, name, voiceid, state, pin1, pin2, pin3);
		case DEVICE_TYPE_AC_TERMAL:
			return new TermalActuator(id, name, voiceid, state, pin1, pin2, pin3, param1);
		case DEVICE_TYPE_SN_TERMAL:
			return new TermalSensor(id, name, voiceid, state, pin1, pin2, pin3);
		case DEVICE_TYPE_SN_LIGHT:
			return new LightSensor(id, name, voiceid, state, pin1, pin2, pin3);
		case DEVICE_TYPE_SN_DOOR:
			return new LightSensor(id, name, voiceid, state, pin1, pin2, pin3);
		default:
			return null;
		}
	}
	
	public static Device getFromDB(String deviceid) {
		DBRequestHandler request = new DBRequestHandler();
		Map<String, Object> values = request.getDevice(deviceid);
		if(!values.isEmpty()){
			String id = values.get("ID_Dispositivo").toString();
			String ambient = (String)values.get("Ambiente");
			int type = Integer.parseInt((String)values.get("Tipo"));
			String voiceid = (String)values.get("Descripcion_Ejec_Voz");
			int intstate = Integer.parseInt((String)values.get("Estado"));
			boolean state = (Boolean)(intstate == 1);
			int pin1 = -1, pin2 = -1, pin3 = -1;
			if(values.get("Pin1") != null) {
				pin1 = (int) values.get("Pin1");
				if(values.get("Pin2") != null) {
					pin2 = (int) values.get("Pin2");
					if(values.get("Pin3") != null) {
						pin3 = (int) values.get("Pin3");
					}
				}
			}
			String param1 = (String)values.get("Param1");
			String param2 = (String)values.get("Param2");
			return Device.createFromType(id, ambient, type, voiceid,
										 state, pin1, pin2, pin3,
										 param1, param2).tagDB();
		}
		return null;
	}
	
	public static Device getFromJson(JsonObject params) {
		if(params.containsKey("name") && params.containsKey("type") && 
		   params.containsKey("voice_id")) {
			
			int pin1 = -1, pin2 = -1, pin3 = -1;
			if(params.containsKey("pin1")) {
				pin1 = params.getInt("pin1");
				if(params.containsKey("pin2")) {
					pin2 = params.getInt("pin2");
					if(params.containsKey("pin3")) {
						pin3 = params.getInt("pin3");
					}
				}
			}
			String subtype = null;
			if(params.containsKey("subtype")){
				subtype = params.getString("subtype");
			}
			
			return Device.createFromType("", params.getString("name"), params.getInt("type"),
										 params.getString("voice_id"), false, pin1, pin2, pin3, 
										 subtype, null);
		}
		return null;
	}
	
	protected boolean mFromDB = false;
	protected String mId = null;
	protected String mName = null;
	protected String mVoiceId = null;
	protected boolean mState = false;
	protected List<Integer> mPins = null;
	protected List<Integer> mPinValues = null;
	protected boolean mBlocked = false;
	
	public Device(String id, String name, String voiceid,
				  boolean state, int pin1, int pin2, int pin3) {
		mId = id;
		mName = name;
		mVoiceId = voiceid;
		mState = state;
		mPins = new ArrayList<Integer>();
		mPinValues = new ArrayList<Integer>();
		if(pin1>=0) {
			mPins.add(0,pin1);
			mPinValues.add(0);
			if(pin2>=0) {
				mPins.add(1,pin2);
				mPinValues.add(0);
				if(pin3>=0) {
					mPins.add(2,pin3);
					mPinValues.add(0);
				}
			}
		}
		mBlocked = false;
	}
	
	protected Device tagDB() {
		mFromDB = true;
		return this;
	}
	
	public boolean commitToDB() {
		DBRequestHandler request = new DBRequestHandler();
		if(mFromDB) {
			return request.updateDevice(this);
		} else {
			return request.addDevice(this);
		}
	}
	
	public boolean deleteFromDB() {
		if(mFromDB) {
			DBRequestHandler request = new DBRequestHandler();
			return request.deleteDevice(mId);
		}
		return true;
	}
	
	public JsonObject asJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder()
									.add("id", mId)
									.add("name", mName)
									.add("type", getClassType())
									.add("voice_id", mVoiceId)
									.add("pin1", getPin(0))
									.add("pin2", getPin(1))
									.add("pin3", getPin(2));
		if(!getParam1().isEmpty()) {
			builder.add("subtype", getParam1());
		}
		if(!getParam2().isEmpty()) {
			builder.add("extra2", getParam1());//rename if necessary
		}
		return builder.build();
	}
	
	public void updateWithParams(JsonObject values, boolean commit) {
		if(values.containsKey("name")) {
			mName = values.getString("name");
		}
		if(values.containsKey("voice_id")) {
			mVoiceId = values.getString("voice_id");
		}
		if(values.containsKey("pin1")) {
			setPin(0, values.getInt("pin1"));
			if(values.containsKey("pin2")) {
				setPin(1, values.getInt("pin2"));
				if(values.containsKey("pin3")) {
					setPin(2, values.getInt("pin3"));
				}
			}
		}
		if(commit) {
			commitToDB();
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
	
	public void setPin(int id, int value) {
		if(mPins.size() > id && id >= 0) {
			mPins.set(id, value);
			mPinValues.set(id, 0);
		}
		else {
			mPins.add(id, value);
			mPinValues.add(id, 0);
		}
	}
	
	public String getId() {
		return mId;
	}
	
	public void setId(String id) {
		mId = id;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public String getVoiceId() {
		return mVoiceId;
	}
	
	public void setVoiceId(String vid) {
		mVoiceId = vid;
	}
	
	public boolean getState() {
		return mState;
	}
	
	public String getParam1() {
		return "";
	}
	
	public String getParam2() {
		return "";
	}
	
	public void lock() {
		mBlocked = true;
	}
	
	public void waitLock() {
		synchronized(this){ 
			while(mBlocked)
				Thread.yield();
		}
	}
	
	public void unlock() {
		mBlocked = false;
	}
	
	public void onOperationResponse(int values[]) {
		for(int i=0;i<values.length;i++) {
			mPinValues.set(i, values[i]);
		}
		unlock();
	}
	
	public abstract boolean setState(boolean state);
	public abstract int getClassType();
	public abstract int getValueType();
}
