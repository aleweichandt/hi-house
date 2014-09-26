package com.web.ones.hihouse;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class User {

	private String user;
	private String password;
	

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
