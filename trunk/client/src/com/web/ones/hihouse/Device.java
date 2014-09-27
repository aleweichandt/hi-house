package com.web.ones.hihouse;

public class Device {
	private String id;
	private String name;
	private String voice;
	private boolean estado;
	
	public Device(String id, String name, String voice, boolean estado) {
		this.id = id;
		this.name = name;
		this.voice = voice;
		this.estado = estado;
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getVoice() {
		return voice;
	}
	public boolean getEstado() {
		return estado;
	}
	
	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	
}
