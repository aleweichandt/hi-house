package server.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.json.JsonObject;

import server.model.devices.Device;

public class AdminRights {
	public AdminRights() {
		
	}
	
//Users
	public List<String> listAllUsers() {
		List<String> ret = new ArrayList<String>();
		DBRequestHandler request = new DBRequestHandler();
		List<Object> ids = request.listAllUsers();
		if(!ids.isEmpty()) {
			for(Iterator<Object> it = ids.iterator(); it.hasNext();) {
				ret.add(it.next().toString());
			}
		}
		return ret;
	}
	
	public User getUser(String userid) {
		return User.getFromDB(userid);
	}
	
	public User addUser(JsonObject params) {
		User usr = User.getFromJson(params);
		if(usr != null) {
			if(!usr.commitToDB()) {
				usr = null;
			}
		}
		return usr;
	}
	
	public boolean deleteUser(String userid) {
		User usr = User.getFromDB(userid);
		if(usr == null){
			return false;
		}
		return usr.deleteFromDB();
	}
	
//Profiles
	public List<String> listAllProfiles() {
		List<String> ret = new ArrayList<String>();
		DBRequestHandler request = new DBRequestHandler();
		List<Object> ids = request.listAllProfiles();
		if(!ids.isEmpty()) {
			for(Iterator<Object> it = ids.iterator(); it.hasNext();) {
				ret.add(it.next().toString());
			}
		}
		return ret;
	}
	
	public Profile getProfile(String profileid) {
		return Profile.getFromDB(profileid);
	}
	
	public Profile addProfile(JsonObject params) {
		Profile prf = Profile.getFromJson(params);
		if(prf != null) {
			if(!prf.commitToDB()) {
				prf = null;
			}
		}
		return prf;
	}
	
	public boolean deleteProfile(String profileid) {
		Profile prf = Profile.getFromDB(profileid);
		if(prf == null){
			return false;
		}
		return prf.deleteFromDB();
	}
	

	
//Devices
	public List<String> listAllDevices() {
		List<String> ret = new ArrayList<String>();
		DBRequestHandler request = new DBRequestHandler();
		List<Object> ids = request.listAllDevices();
		if(!ids.isEmpty()) {
			for(Iterator<Object> it = ids.iterator(); it.hasNext();) {
				ret.add(it.next().toString());
			}
		}
		return ret;
	}
	
	public Device getDevice(String deviceid) {
		return Device.getFromDB(deviceid);
	}
	
	public Device addDevice(JsonObject params) {
		Device dv = Device.getFromJson(params);
		if(dv != null) {
			if(!dv.commitToDB()){
				dv = null;
			}
		}
		return dv;
	}
	
	public boolean deleteDevice(String deviceid) {
		Device dv = Device.getFromDB(deviceid);
		if(dv == null){
			return false;
		}
		return dv.deleteFromDB();
	}
	
//Simulator
	public List<String> listAllSimulators() {
		List<String> ret = new ArrayList<String>();
		DBRequestHandler request = new DBRequestHandler();
		List<Object> ids = request.listAllSimulators();
		if(!ids.isEmpty()) {
			for(Iterator<Object> it = ids.iterator(); it.hasNext();) {
				ret.add(it.next().toString());
			}
		}
		return ret;
	}
	
	public SimulationRoutine getSimulator(String simulatorid) {
		SimulationRoutine sr = SimulationMgr.getInstance().getSimulationWithId(simulatorid);
		if(sr == null){
			return SimulationRoutine.getFromDB(simulatorid);
		}
		return sr;
	}
	
	public boolean addSimulator(String simulatorid, JsonObject params) {
		SimulationRoutine sr = SimulationRoutine.getFromDB(simulatorid);
		if(sr != null){
			return false;
		}
		sr = SimulationRoutine.getFromJson(simulatorid, params);
		if(sr == null) {
			return false;
		}
		return sr.commitToDB();
	}
	
	public boolean deleteSimulator(String simulatorid) {
		SimulationRoutine sr = SimulationMgr.getInstance().getSimulationWithId(simulatorid);
		if(sr == null){
			sr = SimulationRoutine.getFromDB(simulatorid);
			if(sr == null) {
				return false;
			}
		} else {
			SimulationMgr.getInstance().removeSimulation(sr);
		}
		return sr.deleteFromDB();
	}
	
}
