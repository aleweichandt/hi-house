package server.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Device {
	static Device getFromDB(String deviceid) {
		DBRequestHandler request = new DBRequestHandler();
		Map<String, Object> values = request.getDevice(deviceid);
		if(!values.isEmpty()){
			return new Device((String)values.get("ID_Dispositivo"),
							(String)values.get("Ambiente"),
							(String)values.get("Tipo"),
							(String)values.get("Descripcion_Ejec_Voz"),
							((Boolean)values.get("Estado")).booleanValue());
				
		}
		return null;
	}
	
	private String mId = null;
	private String mName = null;
	private String mType = null;
	private String mVoiceId = null;
	private boolean mState = false;
	
	public Device(String id, String name, String type, String voiceid, boolean state) {
		mId = id;
		mName = name;
		mType = type;
		mVoiceId = voiceid;
		mState = state;
	}
}
