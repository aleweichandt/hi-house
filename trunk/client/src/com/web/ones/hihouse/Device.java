package com.web.ones.hihouse;

public class Device {
	private String id;
	private String voice;
	private boolean estado;
	
	public Device(String id, String voice, boolean estado) {
		this.id = id;
		this.voice = voice;
		this.estado = estado;
	}
	public String getId() {
		return id;
	}
	public String getVoice() {
		return voice;
	}
	public boolean getEstado() {
		return estado;
	}
	
}
