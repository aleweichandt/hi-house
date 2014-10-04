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
				ret.add((String)it.next());
			}
		}
		return ret;
	}
	
	public User getUser(String userid) {
		return User.getFromDB(userid);
	}
	
	public boolean addUser(String userid, JsonObject params) {
		User usr = User.getFromDB(userid);
		if(usr != null){
			return false;
		}
		usr = User.getFromJson(userid, params);
		if(usr == null) {
			return false;
		}
		return usr.commitToDB();
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
				ret.add((String)it.next());
			}
		}
		return ret;
	}
	
	public Profile getProfile(String profileid) {
		return Profile.getFromDB(profileid);
	}
	
	public boolean addProfile(String profileid, JsonObject params) {
		Profile prf = Profile.getFromDB(profileid);
		if(prf != null){
			return false;
		}
		prf = Profile.getFromJson(profileid, params);
		if(prf == null) {
			return false;
		}
		return prf.commitToDB();
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
				ret.add((String)it.next());
			}
		}
		return ret;
	}
	
	public Device getDevice(String deviceid) {
		return Device.getFromDB(deviceid);
	}
	
	public boolean addDevice(String deviceid, JsonObject params) {
		Device dv = Device.getFromDB(deviceid);
		if(dv != null){
			return false;
		}
		dv = Device.getFromJson(deviceid, params);
		if(dv == null) {
			return false;
		}
		return dv.commitToDB();
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
				ret.add((String)it.next());
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
