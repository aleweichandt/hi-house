package com.web.ones.hihouse;

import java.util.ArrayList;

public class Profile {
	private String name;
	private ArrayList<Device> devices;
	
	public Profile(String name){
		this.name = name;
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
	
}
