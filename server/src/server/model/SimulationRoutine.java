package server.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;

import server.model.devices.Device;

public class SimulationRoutine {
	public static SimulationRoutine getFromDB(String routineid) {
		DBRequestHandler request = new DBRequestHandler();
		List<Object> result = request.getSimulatorDevices(routineid);
		if(!result.isEmpty()) {
			List<String> deviceList = new ArrayList<String>();
			for(Iterator<Object> it = result.iterator();it.hasNext();) {
				deviceList.add(it.next().toString());
			}
			return new SimulationRoutine(routineid, deviceList).tagDB();
		}
		return null;
	}
	public static SimulationRoutine getFromJson(String routineid, JsonObject params) {
		List<String> deviceList;
		if(params.containsKey("devices")) {
			List<JsonValue> dvsparam = params.getJsonArray("devices");
			deviceList = fillProfileDevices(routineid, dvsparam);
		} else {
			deviceList = new ArrayList<String>();
		}
		return new SimulationRoutine(routineid, deviceList);
	}
	
	private static List<String> fillProfileDevices(String profileid, List<JsonValue> list) {
		List<String> ret = new ArrayList<String>();
		Profile prf = Profile.getFromDB(profileid);
		if(prf != null) {
			List<String> prfDevices = prf.getDevices();
			for(Iterator<JsonValue> it = list.iterator();it.hasNext();) {
				String id = ((JsonNumber)it.next()).toString();
				if(prfDevices.contains(id)) {
					ret.add(id);
				}
			}
		}
		return ret;
	}
	
	private boolean mFromDB = false;
	private String mId = null;
	private List<String> mDevices = null;
	private List<String> mEnabledDevices = null;
	public SimulationRoutine(String id, List<String> devices) {
		mId = id;
		mDevices = new ArrayList<String>(devices);
		mEnabledDevices = new ArrayList<String>();
	}
	
	private SimulationRoutine tagDB() {
		mFromDB = true;
		return this;
	}
	
	public boolean commitToDB() {
		if(mDevices.isEmpty())
			return deleteFromDB();
		
		DBRequestHandler request = new DBRequestHandler();
		if(mFromDB) {
			return request.updateSimulator(this);
		} else {
			return request.addSimulator(this);
		}
	}
	
	public boolean deleteFromDB() {
		if(mFromDB) {
			DBRequestHandler request = new DBRequestHandler();
			return request.deleteSimulator(this);
		}
		return true;
	}
	
	public JsonObject asJson() {
		Profile prf = Profile.getFromDB(mId);
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for(Iterator<String> it = mDevices.iterator();it.hasNext();) {
			Device dv = Device.getFromDB(it.next());
			JsonObject jo = Json.createObjectBuilder().add("id", dv.getId())
								.add("name", dv.getName()).build();
			builder.add(jo);
		}
		return Json.createObjectBuilder()
				   .add("id", mId)
				   .add("name", prf.getName())
				   .add("devices", builder.build()).build();
	}
	
	public void updateWithJson(JsonObject values, boolean commit) {
		if(values.containsKey("devices")) {
			stop();
			List<JsonValue> newlist = values.getJsonArray("devices");
			mDevices.clear();
			mDevices = fillProfileDevices(mId, newlist);
			if(commit) {
				commitToDB();
			}
		}
	}
	
	public void syncWithJson(JsonObject values, boolean commit) {
		if(values.containsKey("devices")) {
			stop();
			List<JsonValue> newlist = values.getJsonArray("devices");
			for(Iterator<JsonValue> it = newlist.iterator();it.hasNext();) {
				JsonNumber value = (JsonNumber) it.next();
				String id = value.toString();
				if(!mDevices.contains(id)) {
					mDevices.remove(id);
				}
			}
			if(commit) {
				commitToDB();
			}
		}
	}
	
	public void removeDeviceWithId(String deviceid) {
		if(mDevices.contains(deviceid)) {
			if(mEnabledDevices.contains(deviceid)) {
				mEnabledDevices.remove(deviceid);
				Device dev = Device.getFromDB(deviceid);
				if(dev != null) {
					dev.setState(false);
				}
			}
			mDevices.remove(deviceid);
		}
	}
	
	public void pushDevice() {
		int index = (int) Math.floor(((Math.random() - 0.001) * (mDevices.size())));
		String toAdd = mDevices.get(index);
		
		if(!mEnabledDevices.contains(toAdd)) {
			mEnabledDevices.add(toAdd);
			
			Device dev = Device.getFromDB(toAdd);
			dev.setState(true);
		}
	}
	
	public void popDevice() {
		if(mEnabledDevices.isEmpty())
			return;
		
		String toRemove = mEnabledDevices.get(0);
		mEnabledDevices.remove(0);
		
		Device dev = Device.getFromDB(toRemove);
		dev.setState(false);
	}
	
	public void stop() {
		while(mEnabledDevices.size() > 0) {
			popDevice();
		}
	}
	
	public String getId() {
		return mId;
	}
	
	public List<String> getDevices() {
		return mDevices;
	}
}
