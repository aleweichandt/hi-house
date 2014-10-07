package server.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

import server.model.devices.Device;

public class Profile {
	public static Profile getFromDB(String profileid) {
		DBRequestHandler request = new DBRequestHandler();
		Map<String, Object> values = request.getProfile(profileid);
		if(!values.isEmpty()){
			return new Profile(values.get("ID_Perfil").toString(),
							(String)values.get("Ambiente"),
							(String)values.get("Descripcion")).tagDB();
				
		}
		return null;
	}
	
	public static Profile getFromJson(JsonObject params) {
		if(params.containsKey("name") && params.containsKey("description")) {
			
			String desc = null;
			if(params.containsKey("description"))
				desc = params.getString("description");
			
			Profile ret = new Profile("", params.getString("name"), desc);
			if(params.containsKey("devices")) {
				List<JsonValue> dvsparam = params.getJsonArray("devices");
				List<String> devices = new ArrayList<String>();
				Iterator<JsonValue> it = dvsparam.iterator();
				while(it.hasNext()) {
					JsonNumber value = (JsonNumber) it.next();
					devices.add(value.toString());
				}
				ret.setDevices(devices);
			}
			return ret;
		}
		return null;
	}
	
	private boolean mFromDB = false;
	private String mId = null;
	private String mName = "";
	private String mDescription = "";
	private List<String> mDevices = null;
	
	public Profile(String id, String name, String description) {
		if(!id.isEmpty()) mId = id;
		if(!name.isEmpty()) mName = name;
		if(description != null) {
			if(!description.isEmpty()) mDescription = description;
		}
		mDevices = new ArrayList<String>();
		
		DBRequestHandler request = new DBRequestHandler();
		List<Object> ids = request.getProfileDeviceIds(id);
		if(!ids.isEmpty()) {
			for(Iterator<Object> it = ids.iterator(); it.hasNext();) {
				mDevices.add(it.next().toString());
			}
		}
	}
	
	private Profile tagDB() {
		mFromDB = true;
		return this;
	}
	
	public boolean commitToDB() {
		DBRequestHandler request = new DBRequestHandler();
		if(mFromDB) {
			return request.updateProfile(this);
		} else {
			return request.addProfile(this);
		}
	}
	
	public boolean deleteFromDB() {
		if(mFromDB) {
			DBRequestHandler request = new DBRequestHandler();
			return request.deleteProfile(mId);
		}
		return true;
	}
	
	public JsonObject asJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder()
									.add("id", mId)
									.add("name", mName)
									.add("description", mDescription);
		return builder.build();
	}
	
	public void updateWithParams(JsonObject values, boolean commit) {
		if(values.containsKey("name")) {
			mName = values.getString("name");
		}
		if(values.containsKey("description")) {
			mDescription = values.getString("description");
		}
		if(values.containsKey("devices")) {
			List<JsonValue> dvsparam = values.getJsonArray("devices");
			List<String> devices = new ArrayList<String>();
			Iterator<JsonValue> it = dvsparam.iterator();
			while(it.hasNext()) {
				JsonNumber value = (JsonNumber) it.next();
				devices.add(value.toString());
			}
			setDevices(devices);
		}
		if(commit) {
			commitToDB();
		}
	}
	
	public Device getDevice(String deviceid) {
		if(mDevices.contains(deviceid)) {
			return Device.getFromDB(deviceid);
		}
		return null;
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
	
	public String getDescription() {
		return mDescription;
	}
	
	public void setDescription(String desc) {
		mDescription = desc;
	}
	
	public List<String> getDevices() {
		return mDevices;
	}
	
	public void setDevices(List<String>devices) {
		mDevices.clear();
		mDevices.addAll(devices);
	}
}
