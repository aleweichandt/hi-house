package com.web.ones.hihouse;

public class Device {
	
	public static final int DEVICE_TYPE_SN_TERMAL = 0;
	public static final int DEVICE_TYPE_SN_LIGHT = 1;
	public static final int DEVICE_TYPE_AC_LIGHT = 2;
	public static final int DEVICE_TYPE_AC_TERMAL = 3;
	public static final int DEVICE_TYPE_AC_DOOR = 4;
	public static final int DEVICE_TYPE_SN_DOOR = 5;
	
	private int id;
	private String name;
	private String voice;
	private boolean state;
	private int type;
	
	public Device(int id, String name, String voice, boolean estado, int type) {
		this.id = id;
		this.name = name;
		this.voice = voice;
		this.state = estado;
		this.type = type;
	}
	public Device(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getVoice() {
		return voice;
	}
	public boolean getState() {
		return state;
	}
	public int getType(){
		return type;
	}
	
	public void setState(boolean state) {
		this.state = state;
	}
	
	public boolean setDeviceState(int id, boolean state) {
		if(this.id==id){
			this.state = state;
			return true;
		}
		return false;
	}
	
}
