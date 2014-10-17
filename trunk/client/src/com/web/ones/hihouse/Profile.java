package com.web.ones.hihouse;

import java.util.ArrayList;

public class Profile {
	private int id;
	private String name;
	private ArrayList<Device> devices;
	
	public Profile(String name) {
		this.name = name;
		devices = new ArrayList<Device>();
	}
	public Profile(int id, String name){
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
	public int getId(){
		return id;
	}
	
	public Device getDeviceByVoiceDesc(String voice_desc){
		for(Device d : devices){
			if(d.getVoice().equals(voice_desc)) return d;
		}
		return null;
	}

	public boolean setDeviceState(int id, boolean state) {
		for(Device d : devices){
			if(d.setDeviceState(id, state)) return true;
		}
		return false;
	}
}
