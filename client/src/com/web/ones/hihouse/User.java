package com.web.ones.hihouse;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class User {

	private String user;
	private String password;
	private ArrayList<Profile> profiles;
	
	public User(){
		profiles = new ArrayList<Profile>();
	}
	
	public void addProfile(Profile p){
		profiles.add(p);
	}

	public String getDeviceByVoiceDesc(String voice_desc){
		for(Profile p : profiles){
			String id = p.getDeviceByVoiceDesc(voice_desc);
			if(id!=null) return id;
		}
		return null;
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
}
