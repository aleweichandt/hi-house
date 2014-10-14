package com.web.ones.hihouse;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;


public class User {
	
	private int id;
	private String user;
	private String password;
	private String token;
	private boolean admin;
	private ArrayList<Profile> profiles;
	private boolean allowNotifications;
	
	public User(int id, String user, String pass, String token, boolean admin){
		profiles = new ArrayList<Profile>();
		this.id = id;
		this.user = user;
		this.password = pass;
		this.token = token;
		this.admin = admin;
		this.allowNotifications = false;
	}
	
	public User(int id, String user){
		profiles = new ArrayList<Profile>();
		this.user = user;
		this.id = id;
		this.allowNotifications = false;
	}
	
	public int getId(){
		return id;
	}
	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getToken() {
		return token;
	}
	
	public boolean isAdmin() {
		return admin;
	}
	public ArrayList<Profile> getProfiles(){
		return profiles;
	}

	private void addProfile(Profile p){
		profiles.add(p);
	}
	
	private void setProfiles(ArrayList<Profile> p){
		profiles = p;
	}

	public int getDeviceByVoiceDesc(String voice_desc){
		for(Profile p : profiles){
			int id = p.getDeviceByVoiceDesc(voice_desc);
			if(id!=-1) return id;
		}
		return -1;
	}
	
	public boolean setProfilesAndDevices(String jsonStr){
		ArrayList<Profile> profiles = new ArrayList<Profile>();
		JSONObject reader, device;
    	JSONArray prof;
    	String perfil_name;
		try {
			reader = new JSONObject(jsonStr);
			Iterator<?> keys = reader.keys();
			while(keys.hasNext()){//itero sobre los perfiles
				perfil_name = (String)keys.next();
				Profile p = new Profile(perfil_name);
				prof = reader.getJSONArray(perfil_name);
				for(int i=0; i<prof.length(); i++){//itero sobre los dispositivos
					device = prof.getJSONObject(i);
					Device d = new Device(device.getInt("id"), device.getString("name"), device.getString("voice_id"), device.getBoolean("state"), device.getInt("type"));
					p.addDevice(d);
				}
				profiles.add(p);
			}
			this.setProfiles(profiles);
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean updateDevice(String jsonStr) {
		JSONObject device;
		boolean result = false;
		try{
			device = new JSONObject(jsonStr);
			int id = device.getInt("id");
			boolean state = device.getBoolean("state");
			for(Profile p : profiles){
				if(p.setDeviceState(id, state)) result = true;
			}
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void guardarDatos() {
		//decian los tutos con get activity pero no gustaba, por los fragments?
		Context context = null ;
		
		SharedPreferences sharedPref = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("usuario", user);
		editor.putString("nombre", password);
		editor.commit();
	}	
	
	public void borrarDatos() {
		Context context = null ;
		
		SharedPreferences sharedPref = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("usuario", "");
		editor.putString("nombre", "");
		editor.commit();	
	}

	public void setAllowNotifications(boolean enabled) {
		allowNotifications = enabled;
	}
	
	public boolean canReceiveNotifications() {
		return allowNotifications;
	}
}
