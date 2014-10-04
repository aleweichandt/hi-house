package com.web.ones.hihouse;

import java.util.ArrayList;

public class Profile {
	private String id;
	private String name;
	private ArrayList<Device> devices;
	
	public Profile(String name){
		this.name = name;
		devices = new ArrayList<Device>();
	}
	public Profile(String id, String name){
		this.id = id;
		this.name = name;
		devices = new ArrayList<Device>();
	}

	public ArrayList<Device> getDevices() {
		return devices;
	}

	public void addDevice(Device device) {
		devices.add(device);
	}

	public String getName() {
		return name;
	}
	public String getId(){
		return id;
	}
	
	public String getDeviceByVoiceDesc(String voice_desc){
		for(Device d : devices){
			if(d.getVoice().equals(voice_desc)) return d.getId();
		}
		return null;
	}

	public boolean setDeviceState(String id, boolean state) {
		for(Device d : devices){
			if(d.setDeviceState(id, state)) return true;
		}
		return false;
	}
}
